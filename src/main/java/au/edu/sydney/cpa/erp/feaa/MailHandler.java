package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;


public class MailHandler extends ContactHandler {

    public boolean sendInvoice(AuthToken token, Client client, String data) {

            String address = client.getAddress();
            String suburb = client.getSuburb();
            String state = client.getState();
            String postcode = client.getPostCode();
            if (null != address && null != suburb &&
                    null != state && null != postcode) {
                Mail.sendInvoice(token, client.getFName(), client.getLName(), data, address, suburb, state, postcode);
                return true;
            }
        return false;
    }
}
