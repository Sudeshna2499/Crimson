package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.ordering.Client;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.feaa.ordering.*;
import au.edu.sydney.cpa.erp.feaa.reports.ReportDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("Duplicates")
public class FEAAFacade {
    private AuthToken token;

    //To keep track of clients that have already been loaded from the database
    private HashMap<Integer, Client> existingClientList = new HashMap<>();

    public boolean login(String userName, String password) {
        token = AuthModule.login(userName, password);

        return null != token;
    }

    public List<Integer> getAllOrders() {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();

        List<Order> orders = database.getOrders(token);

        List<Integer> result = new ArrayList<>();

        for (Order order : orders) {
            result.add(order.getOrderID());
        }

        return result;
    }

    public Integer createOrder(int clientID, LocalDateTime date, boolean isCritical, boolean isScheduled, int orderType, int criticalLoadingRaw, int maxCountedEmployees, int numQuarters) {
        if (null == token) {
            throw new SecurityException();
        }

        double criticalLoading = criticalLoadingRaw / 100.0;

        Order order;

        if (!TestDatabase.getInstance().getClientIDs(token).contains(clientID)) {
            throw new IllegalArgumentException("Invalid client ID");
        }

        int id = TestDatabase.getInstance().getNextOrderID();

        if (isScheduled) {
            if (1 == orderType) { // 1 is regular accounting
                if (isCritical) {
                    order = new FirstOrderTypeScheduled(id, clientID, date, criticalLoading, maxCountedEmployees, numQuarters);
                } else {
                    order = new Order66Scheduled(id, clientID, date, maxCountedEmployees, numQuarters);
                }
            } else if (2 == orderType) { // 2 is audit
                    if (isCritical) {
                        order = new CriticalAuditOrderScheduled(id, clientID, date, criticalLoading, numQuarters);
                    } else {
                        order = new NewOrderImplScheduled(id, clientID, date, numQuarters);
                    }
            } else {return null;}
        } else {
            if (1 == orderType) {
                if (isCritical) {
                    order = new FirstOrderType(id, clientID, date, criticalLoading, maxCountedEmployees);
                } else {
                    order = new Order66(id, clientID, date, maxCountedEmployees);
                }
            } else if (2 == orderType) {
                if (isCritical) {
                    order = new CriticalAuditOrder(id, clientID, date, criticalLoading);
                } else {
                    order = new NewOrderImpl(id, clientID, date);
                }
            } else {return null;}
        }

        TestDatabase.getInstance().saveOrder(token, order);
        return order.getOrderID();
    }

    public List<Integer> getAllClientIDs() {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();
        return database.getClientIDs(token);
    }

    //using lazy initialisation to load clients from the database. Will lag only for new client ids but existing ones will load faster
    public Client getClient(int id) {

        if (null == token) {
            throw new SecurityException();
        }

        //This checks whether the client has already been loaded from the database before. If so, return the existing data on that client
        if(existingClientList.containsKey(id)){
            return existingClientList.get(id);
        }
        //If client id does not exist, then this is a new client. Load them from the database a store them in a hashtable for future reference.
        else{
            Client newClient = new ClientImpl(token, id);
            existingClientList.put(id, newClient);
            return newClient;
        }

    }

    public boolean removeOrder(int id) {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();
        return database.removeOrder(token, id);
    }

    public List<Report> getAllReports() {
        if (null == token) {
            throw new SecurityException();
        }

        return new ArrayList<>(ReportDatabase.getTestReports());
    }

    //using chain of responsibility to reduce the amount of work done in ContactHandler
    public boolean finaliseOrder(int orderID, List<String> contactPriority) {
        if (null == token) {
            throw new SecurityException();
        }

//        List<Boolean> contactPriorityAsMethods = new ArrayList<>();
        List<ContactHandler> contactMethods = new ArrayList<>();

        Order order = TestDatabase.getInstance().getOrder(token, orderID);

        if (null != contactPriority) {
            for (String method: contactPriority) {
                switch (method.toLowerCase()) {

                    case "internal accounting":
                        contactMethods.add(new InternalAccountingHandler());
                        break;

                    case "email":
                        contactMethods.add(new EmailHandler());
                        break;

                    case "carrier pigeon":
                        contactMethods.add(new CarrierPigeonHandler());
                        break;

                    case "mail":
                        contactMethods.add(new MailHandler());
                        break;

                    case "phone call":
                        contactMethods.add(new PhoneCallHandler());
                        break;

                    case "sms":
                        contactMethods.add(new SMSHandler());
                        break;

                    default:
                        break;
                }
            }
        }
        else {
            contactMethods.add(new SMSHandler());
            contactMethods.add(new InternalAccountingHandler());
            contactMethods.add(new CarrierPigeonHandler());
            contactMethods.add(new PhoneCallHandler());
            contactMethods.add(new MailHandler());
            contactMethods.add(new EmailHandler());
        }

        order.finalise();
        TestDatabase.getInstance().saveOrder(token, order);

        for(int i =0; i<contactMethods.size();i++){
            boolean res = contactMethods.get(i).sendInvoice(token, getClient(order.getClient()),order.generateInvoiceData());

            if(res==true){
                return true;
            }
        }
        return false;
    }

    public void logout() {
        AuthModule.logout(token);
        token = null;
    }

    public double getOrderTotalCommission(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);
        if (null == order) {
            return 0.0;
        }

        return order.getTotalCommission();
    }

    public void orderLineSet(int orderID, Report report, int numEmployees) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);

        if (null == order) {
            System.out.println("got here");
            return;
        }

        order.setReport(report, numEmployees);

        TestDatabase.getInstance().saveOrder(token, order);
    }

    public String getOrderLongDesc(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);

        if (null == order) {
            return null;
        }

        return order.longDesc();
    }

    public String getOrderShortDesc(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);

        if (null == order) {
            return null;
        }

        return order.shortDesc();
    }

    public List<String> getKnownContactMethods() {
        if (null == token) {
            throw new SecurityException();
        }
        return ContactHandler.getKnownMethods();
    }
}
