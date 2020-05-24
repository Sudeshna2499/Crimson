package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ScheduledImpl implements Scheduled {

    private int numQuarters;

    public ScheduledImpl(int numQuarters){
        this.numQuarters=numQuarters;
    }

    public int getNumQuarters(){
        return numQuarters;
    }
    @Override
    public double getTotalCommission(double commission) {
        return commission;
    }

    @Override
    public String longDesc(Map<Report, Integer> reports, double commission, int id, LocalDateTime date, boolean finalised,
                           Critical criticalType, OrderType type, int maxCountedEmployees) {

        double totalBaseCost = 0.0;
        double loadedCostPerQuarter = commission;
        double totalLoadedCost = this.getTotalCommission(commission);
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {

            double subtotal;
            if(type instanceof RegularOrder){
                subtotal = report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));
                totalBaseCost += subtotal;
            }
            else{
                subtotal = report.getCommission() * reports.get(report);
                totalBaseCost += subtotal;
            }

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f",
                    report.getReportName(),
                    reports.get(report),
                    report.getCommission(),
                    subtotal));

            if(type instanceof RegularOrder){
                if (reports.get(report) > maxCountedEmployees) {
                    reportSB.append(" *CAPPED*\n");
                } else {
                    reportSB.append("\n");
                }
            }
            else{
                reportSB.append("\n");
            }

        }

        if(criticalType instanceof CriticalImpl){
            return String.format(finalised ? "" : "*NOT FINALISED*\n" +
                            "Order details (id #%d)\n" +
                            "Date: %s\n" +
                            "Number of quarters: %d\n" +
                            "Reports:\n" +
                            "%s" +
                            "Critical Loading: $%,.2f\n" +
                            "Recurring cost: $%,.2f\n" +
                            "Total cost: $%,.2f\n",
                    id,
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    numQuarters,
                    reportSB.toString(),
                    totalLoadedCost - (totalBaseCost * numQuarters),
                    loadedCostPerQuarter/numQuarters,
                    totalLoadedCost
            );
        }
        else{
            return String.format(finalised ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Number of quarters: %d\n" +
                        "Reports:\n" +
                        "%s" +
                        "Recurring cost: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                numQuarters,
                reportSB.toString(),
                commission/numQuarters,
                totalLoadedCost
        );
        }
    }

    @Override
    public String shortDesc(int id, double recurCost, double commission) {

        return String.format("ID:%s $%,.2f per quarter, $%,.2f total", id, recurCost, getTotalCommission(commission));
    }

    @Override
    public String generateInvoiceData(double recurCost, int numQuarters, double commission) {

        return String.format("Your priority business account will be charged: $%,.2f each quarter for %d quarters, with a total overall cost of: $%,.2f" +
               "\nPlease see your internal accounting department for itemised details.", recurCost, numQuarters, getTotalCommission(commission));

    }
}
