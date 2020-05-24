package au.edu.sydney.cpa.erp.feaa.ordering;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.util.Map;

public class CriticalImpl implements Critical {

    private double criticalLoading;

    public CriticalImpl(double criticalLoading){
        this.criticalLoading=criticalLoading;
    }

    @Override
    public double getCriticalLoading() {
        return criticalLoading;
    }

    @Override
    public double getTotalCommission(double cost) {
        return cost * criticalLoading;
    }


    @Override
    public String generateInvoiceData(Map<Report, Integer> reports, double commission, int numQuarters,
                                      Scheduled scheduledType, double recurCost, int maxCountedEmployees, OrderType type) {

       return scheduledType.generateInvoiceData(recurCost, numQuarters, commission);

    }
}
