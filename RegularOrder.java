package au.edu.sydney.cpa.erp.feaa.ordering;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;

public class RegularOrder extends OrderType{

    private Critical criticalType;
    private Scheduled scheduledType;
    private int numQuarters;

    public RegularOrder(int id, int client, LocalDateTime date, double criticalLoading,
                        int maxCountedEmployees, Critical criticalType,Scheduled scheduledType, int numQuarters) {
        super(id, client, date, criticalLoading, maxCountedEmployees);
        this.criticalType=criticalType;
        this.scheduledType=scheduledType;
        this.numQuarters=numQuarters;
    }

    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));
        }

        cost += criticalType.getTotalCommission(cost);
        return cost*scheduledType.getNumQuarters();
    }

    @Override
    public String generateInvoiceData() {

        double commission = getTotalCommission();
        return criticalType.generateInvoiceData(reports, commission, numQuarters, scheduledType,getRecurringCost());
    }

    @Override
    public Order copy() {

        Order newOrder = new RegularOrder(id, client, date, criticalLoading,
                maxCountedEmployees, criticalType, scheduledType, numQuarters);
        return newOrder;
    }

    @Override
    public String longDesc() {
        return criticalType.longDesc(reports, finalised, id, date, getTotalCommission(), scheduledType);
    }

    @Override
    public double getRecurringCost() {
        return 0;
    }

    @Override
    public int getNumberOfQuarters() {
        return 0;
    }

    @Override
    public String shortDesc() {
        return scheduledType.shortDesc(id,getRecurringCost(),getTotalCommission());
    }
}