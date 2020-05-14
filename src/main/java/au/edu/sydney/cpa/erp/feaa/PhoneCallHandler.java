package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;


public class PhoneCallHandler extends ContactHandler {

    public boolean sendInvoice(AuthToken token, Client client, String data) {

            String phone = client.getPhoneNumber();
            if (null != phone) {
                PhoneCall.sendInvoice(token, client.getFName(), client.getLName(), data, phone);
                return true;
            }
        return false;
    }
}
