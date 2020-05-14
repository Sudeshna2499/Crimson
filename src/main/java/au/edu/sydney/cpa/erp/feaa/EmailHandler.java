package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;


public class EmailHandler extends ContactHandler {

    public  boolean sendInvoice(AuthToken token, Client client, String data) {

            String email = client.getEmailAddress();
            if (null != email) {
                Email.sendInvoice(token, client.getFName(), client.getLName(), data, email);
                return true;
            }
        return false;
    }
}
