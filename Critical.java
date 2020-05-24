package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.util.Map;

public interface Critical {

    public double getCriticalLoading();
    public String longDesc(Map<Report, Integer> reports, boolean finalised, int id,
                           LocalDateTime date, double commission, Scheduled scheduledType);
    public double getTotalCommission(double cost);
    public String generateInvoiceData(Map<Report, Integer> reports, double commission, int numQuarters, Scheduled scheduledType, double recurCost);
}
