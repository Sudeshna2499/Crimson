package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;

public class CarrierPigeonHandler extends ContactHandler {

    public  boolean sendInvoice(AuthToken token, Client client, String data) {

            String pigeonCoopID = client.getPigeonCoopID();
            if (null != pigeonCoopID) {
                CarrierPigeon.sendInvoice(token, client.getFName(), client.getLName(), data, pigeonCoopID);
                return true;
            }
        return false;
    }
}
