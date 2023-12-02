package application.persistance.repository;

import application.model.Card;

public interface PackageRepositoryInterface {
    public boolean createPackage(Card cards[]);
    public boolean acquirePackage();
    public boolean checkAuthentication(String token);
}
