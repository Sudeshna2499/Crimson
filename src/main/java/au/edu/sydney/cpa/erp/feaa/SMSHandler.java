package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;


public class SMSHandler extends ContactHandler {

    public boolean sendInvoice(AuthToken token, Client client, String data) {

            String smsPhone = client.getPhoneNumber();
            if (null != smsPhone) {
                SMS.sendInvoice(token, client.getFName(), client.getLName(), data, smsPhone);
                return true;
            }
        return false;
    }
}
