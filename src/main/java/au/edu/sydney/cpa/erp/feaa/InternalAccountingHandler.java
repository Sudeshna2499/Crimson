package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;


public class InternalAccountingHandler extends ContactHandler {

    public boolean sendInvoice(AuthToken token, Client client, String data) {

            String internalAccounting = client.getInternalAccounting();
            String businessName = client.getBusinessName();
            if (null != internalAccounting && null != businessName) {
                InternalAccounting.sendInvoice(token, client.getFName(), client.getLName(), data, internalAccounting,businessName);
                return true;
            }

        return false;
    }
}
