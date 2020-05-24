package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.util.Map;

public interface Scheduled {

    public double getTotalCommission(double commission);
    public String longDesc(Map<Report, Integer> reports, double commission,
                           int id, LocalDateTime date, boolean finalised,
                           Critical criticalType, OrderType type, int maxCountedEmployees);
    public String shortDesc(int id, double recurCost, double commission);
    public String generateInvoiceData(double recurCost, int numQuarters, double commission);
    public int getNumQuarters();
}
