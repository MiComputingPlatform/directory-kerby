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
package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1GeneralizedTime;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1SequenceType;

/**
 * KEKIdentifier ::= SEQUENCE {
 *   keyIdentifier OCTET STRING,
 *   date GeneralizedTime OPTIONAL,
 *   other OtherKeyAttribute OPTIONAL }
 */
public class KEKIdentifier extends Asn1SequenceType {
    protected enum KEKIdentifierField implements EnumType {
        KEY_IDENTIFIER,
        DATE,
        OTHER;

        @Override
        public int getValue() {
            return ordinal();
        }

        @Override
        public String getName() {
            return name();
        }
    }

    static Asn1FieldInfo[] fieldInfos = new Asn1FieldInfo[] {
            new Asn1FieldInfo(KEKIdentifierField.KEY_IDENTIFIER, Asn1OctetString.class),
            new Asn1FieldInfo(KEKIdentifierField.DATE, Asn1GeneralizedTime.class),
            new Asn1FieldInfo(KEKIdentifierField.OTHER, OtherKeyAttribute.class)
    };

    public KEKIdentifier() {
        super(fieldInfos);
    }

    public Asn1OctetString getKeyIdentifier() {
        return getFieldAs(KEKIdentifierField.KEY_IDENTIFIER, Asn1OctetString.class);
    }

    public void setKeyIdentifier(Asn1OctetString keyIdentifier) {
        setFieldAs(KEKIdentifierField.KEY_IDENTIFIER, keyIdentifier);
    }

    public Asn1GeneralizedTime getDate() {
        return getFieldAs(KEKIdentifierField.DATE, Asn1GeneralizedTime.class);
    }

    public void setDate(Asn1GeneralizedTime date) {
        setFieldAs(KEKIdentifierField.DATE, date);
    }

    public OtherKeyAttribute getOther() {
        return getFieldAs(KEKIdentifierField.OTHER, OtherKeyAttribute.class);
    }

    public void setOther(OtherKeyAttribute other) {
        setFieldAs(KEKIdentifierField.OTHER, other);
    }
}
