/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacmlatt.pdp.std.combiners;

import java.util.Iterator;
import java.util.List;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.eval.EvaluationException;
import com.att.research.xacmlatt.pdp.eval.EvaluationResult;
import com.att.research.xacmlatt.pdp.policy.CombinerParameter;
import com.att.research.xacmlatt.pdp.policy.CombiningElement;
import com.att.research.xacmlatt.pdp.policy.Rule;
import com.att.research.xacmlatt.pdp.policy.RuleEffect;

/**
 * LegacyPermitOverridesRule extends {@link com.att.research.xacmlatt.pdp.policy.combiners.CombiningAlgorithmBase} for
 * {@link com.att.research.xacmlatt.pdp.policy.Rule}s to implement the XACML 1.0 permit-overrides rule combining algorithm.
 *
 * @author car
 * @version $Revision: 1.1 $
 */
public class LegacyPermitOverridesRule extends CombiningAlgorithmBase<Rule> {

    public LegacyPermitOverridesRule(Identifier identifierIn) {
        super(identifierIn);
    }

    @Override
    public EvaluationResult combine(EvaluationContext evaluationContext, List<CombiningElement<Rule>> elements, List<CombinerParameter> combinerParameters) throws EvaluationException {
        boolean atLeastOneDeny							= false;
        boolean potentialPermit							= false;

        EvaluationResult evaluationResultCombined		= new EvaluationResult(Decision.DENY);
        EvaluationResult evaluationResultIndeterminate	= null;

        Iterator<CombiningElement<Rule>> iterElements	= elements.iterator();
        while (iterElements.hasNext()) {
            CombiningElement<Rule> combiningElement		= iterElements.next();
            EvaluationResult evaluationResultElement	= combiningElement.evaluate(evaluationContext);

            assert(evaluationResultElement != null);
            switch(evaluationResultElement.getDecision()) {
            case DENY:
                atLeastOneDeny	= true;
                evaluationResultCombined.merge(evaluationResultElement);
                break;
            case INDETERMINATE:
            case INDETERMINATE_DENYPERMIT:
            case INDETERMINATE_DENY:
            case INDETERMINATE_PERMIT:
                if (evaluationResultIndeterminate == null) {
                    evaluationResultIndeterminate	= evaluationResultElement;
                } else {
                    evaluationResultIndeterminate.merge(evaluationResultElement);
                }
                if (combiningElement.getEvaluatable().getRuleEffect() == RuleEffect.PERMIT) {
                    potentialPermit	= true;
                }
                break;
            case NOTAPPLICABLE:
                break;
            case PERMIT:
                return evaluationResultElement;
            default:
                throw new EvaluationException("Illegal Decision: \"" + evaluationResultElement.getDecision().toString());
            }
        }

        if (potentialPermit) {
            return evaluationResultIndeterminate;
        } else if (atLeastOneDeny) {
            return evaluationResultCombined;
        } else if (evaluationResultIndeterminate != null) {
            return evaluationResultIndeterminate;
        } else {
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }
    }

}
