/**
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
package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.client.KrbKdcOption;
import org.apache.kerby.kerberos.kerb.common.CheckSumUtil;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.ap.ApOptions;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.ticket.KrbTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;

public class TgsRequestWithTgt extends TgsRequest {

    private ApReq apReq;
    private KrbTicket ticket;
    private PrincipalName clientPrincipal;

    public TgsRequestWithTgt(KrbContext context, TgtTicket tgt) {
        super(context);
        setAllowedPreauth(PaDataType.TGS_REQ);
        ticket = tgt;
        clientPrincipal = tgt.getClientPrincipal();
    }

    public TgsRequestWithTgt(KrbContext context, SgtTicket sgt) {
        super(context);
        setAllowedPreauth(PaDataType.TGS_REQ);
        ticket = sgt;
        clientPrincipal = sgt.getClientPrincipal();
    }

    public PrincipalName getClientPrincipal() {
        return clientPrincipal;
    }

    @Override
    public EncryptionKey getClientKey() throws KrbException {
        return getSessionKey();
    }

    @Override
    public EncryptionKey getSessionKey() {
        return ticket.getSessionKey();
    }

    private ApReq makeApReq() throws KrbException {
        ApReq apReq = new ApReq();

        Authenticator authenticator = makeAuthenticator();
        EncryptionKey sessionKey = ticket.getSessionKey();
        EncryptedData authnData = EncryptionUtil.seal(authenticator,
            sessionKey, KeyUsage.TGS_REQ_AUTH);
        apReq.setEncryptedAuthenticator(authnData);
        apReq.setAuthenticator(authenticator);
        apReq.setTicket(ticket.getTicket());
        ApOptions apOptions = new ApOptions();
        apReq.setApOptions(apOptions);

        return apReq;
    }

    public ApReq getApReq() throws KrbException {
        if (apReq == null) {
            apReq = makeApReq();
        }
        return apReq;
    }

    private Authenticator makeAuthenticator() throws KrbException {
        Authenticator authenticator = new Authenticator();
        authenticator.setAuthenticatorVno(5);
        authenticator.setCname(clientPrincipal);
        authenticator.setCrealm(clientPrincipal.getRealm());
        authenticator.setCtime(KerberosTime.now());
        authenticator.setCusec(0);
        authenticator.setSubKey(ticket.getSessionKey());
        KerberosTime renewTill = null;

        if (getRequestOptions().contains(KrbKdcOption.RENEW)) {
            renewTill = ticket.getEncKdcRepPart().getRenewTill();
        }
        KdcReqBody reqBody = getReqBody(renewTill);

        CheckSum checksum = CheckSumUtil.seal(reqBody, null,
            ticket.getSessionKey(), KeyUsage.TGS_REQ_AUTH_CKSUM);
        authenticator.setCksum(checksum);

        return authenticator;
    }
}
