package facade;

import db.SalesDAO;
import java.util.ArrayList;
import java.util.List;

public class MostPurchasedFacade {

    public List<String[]> getTopPurchasedMedicines(int limit) {
        List<String[]> formattedData = new ArrayList<>();
        List<String[]> rawData = SalesDAO.getMostPurchasedOverall(limit);

        for (String[] row : rawData) {
            formattedData.add(new String[]{row[0], row[2], row[3]});
        }
        
        return formattedData;
    }

    public List<String[]> getTopMedicinesByCompany() {
        return SalesDAO.getMostPurchasedByCompany();
    }
    public List<String[]> getTopSpendingCustomers(int limit) {
        List<String[]> formattedData = new ArrayList<>();
        List<String[]> rawData = SalesDAO.getTopSpendingCustomers(limit);
        
        for (String[] row : rawData) {
            formattedData.add(new String[]{row[0], row[1], row[2], row[3]});
        }
        
        return formattedData;
    }
}