package au.edu.sydney.cpa.erp.feaa.ordering;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;

public class AuditOrder extends OrderType{

    private Critical criticalType;
    private Scheduled scheduledType;
    private int numQuarters;

    public AuditOrder(int id, int client, LocalDateTime date, double criticalLoading,
                      int maxCountedEmployees, Critical criticalType, Scheduled scheduledType, int numQuarters) {
        super(id, client, date, criticalLoading, maxCountedEmployees);
        this.criticalType=criticalType;
        this.scheduledType=scheduledType;
        this.numQuarters=numQuarters;
    }

    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += reports.get(report) * report.getCommission();
        }
        cost +=criticalType.getTotalCommission(cost);
        return cost*scheduledType.getNumQuarters();
    }

    @Override
    public String generateInvoiceData() {
        return criticalType.generateInvoiceData(reports, getTotalCommission(), numQuarters, scheduledType, getRecurringCost());
    }

    @Override
    public Order copy() {

        Order newOrder = new AuditOrder(id, client, date, criticalLoading,
                maxCountedEmployees, criticalType, scheduledType, numQuarters);
        return newOrder;
    }

    @Override
    public String longDesc() {
        if(scheduledType instanceof ScheduledImpl){
            return scheduledType.longDesc(reports,getTotalCommission(),id,date,finalised, criticalType);
        }
        return criticalType.longDesc(reports, finalised, id, date, scheduledType.getTotalCommission(getTotalCommission()), scheduledType);
    }

    @Override
    public double getRecurringCost() {
        return getTotalCommission();
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