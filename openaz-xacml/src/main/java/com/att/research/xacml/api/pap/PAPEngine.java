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
package com.att.research.xacml.api.pap;

import java.io.InputStream;
import java.util.Set;

public interface PAPEngine {


    //
    // Group-level operations
    //

    public PDPGroup					getDefaultGroup() throws PAPException;

    public void						SetDefaultGroup(PDPGroup group) throws PAPException;

    public Set<PDPGroup>			getPDPGroups() throws PAPException;

    public PDPGroup					getGroup(String id) throws PAPException;

    public void						newGroup(String name, String description) throws PAPException, NullPointerException;

    public void						updateGroup(PDPGroup group) throws PAPException;

    /**
     * @param group - to delete
     * @param newGroup - Group where any PDP's in the group will be moved to. Can be NULL if there are no PDP's in the group.
     * @throws PAPException
     * @throws NullPointerException
     */
    public void						removeGroup(PDPGroup group, PDPGroup newGroup) throws PAPException, NullPointerException;

    public PDPGroup					getPDPGroup(PDP pdp) throws PAPException;

    public PDPGroup					getPDPGroup(String pdpId) throws PAPException;

    /**
     * Special case - for REST this contacts the PDP for status Details, but the non-RESTful version is never called
     */
    public PDPStatus				getStatus(PDP pdp) throws PAPException;



    //
    // PDP operations
    //

    public PDP						getPDP(String pdpId) throws PAPException;

    public void						newPDP(String id, PDPGroup group, String name, String description) throws PAPException, NullPointerException;

    public void						movePDP(PDP pdp, PDPGroup newGroup) throws PAPException;

    public void						updatePDP(PDP pdp) throws PAPException;

    public void						removePDP(PDP pdp) throws PAPException;


    //
    // Policy operations
    //

//TODO - This is a degenerate form for making group-level changes because it makes only one change to one file.
//TODO		We really want to be able to make multiple changes to the group in one operation, including changing a file from root to referenced and back.
//TODO	This method should be replaced with two methods:
//TODO		public boolean copyFile(String policyId, PDPGroup group, InputStream policy) throws PAPException
//TODO				(just copy the file and throw exception if the copy fails)
//TODO		public PDPGroup updateGroup(group) throws PAPException
//TODO   			where the group has been changed locally to the way we want it to be on the server.
    public void						publishPolicy(String id, String name, boolean isRoot, InputStream policy, PDPGroup group) throws PAPException;

    // copy the given policy file into the group's directory, but do not include the policy in the group's policy set
    public void						copyPolicy(PDPPolicy policy, PDPGroup group) throws PAPException;

    public void						removePolicy(PDPPolicy policy, PDPGroup group) throws PAPException;

}
