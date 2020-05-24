package au.edu.sydney.cpa.erp.feaa.ordering;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;

public class AuditOrder extends OrderType{

    private Critical criticalType;
    private Scheduled scheduledType;
    private int numQuarters;
    private double cost;

    public AuditOrder(int id, int client, LocalDateTime date, double criticalLoading,
                      int maxCountedEmployees, Critical criticalType, Scheduled scheduledType, int numQuarters) {
        super(id, client, date, criticalLoading, maxCountedEmployees);
        this.criticalType=criticalType;
        this.scheduledType=scheduledType;
        this.numQuarters=numQuarters;
        this.cost=0.0;
    }

    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += reports.get(report) * report.getCommission();
        }
        cost +=criticalType.getTotalCommission(cost);
        if(scheduledType instanceof ScheduledImpl){
            return cost*numQuarters;
        }
        return cost;
    }

    @Override
    public String generateInvoiceData() {
       // return scheduledType.generateInvoiceData(getRecurringCost(),numQuarters,getTotalCommission());
        return criticalType.generateInvoiceData(reports, getTotalCommission(), numQuarters, scheduledType, getRecurringCost(), maxCountedEmployees, this);
    }

    @Override
    public Order copy() {

        Order newOrder = new AuditOrder(id, client, date, criticalLoading,
                maxCountedEmployees, criticalType, scheduledType, numQuarters);
        return newOrder;
    }

    @Override
    public String longDesc() {
        return scheduledType.longDesc(reports,getTotalCommission(),id,date,finalised, criticalType, this, maxCountedEmployees);
    }

    @Override
    public double getRecurringCost() {
        return getTotalCommission()/numQuarters;
    }

    @Override
    public int getNumberOfQuarters() {
        return scheduledType.getNumQuarters();
    }

    @Override
    public String shortDesc() {

        return scheduledType.shortDesc(id,getRecurringCost(),scheduledType.getTotalCommission(getTotalCommission()));
    }
}