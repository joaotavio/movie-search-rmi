package moviesearch.service;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MovieSearch extends Remote {

    List<Movie> search(String name) throws RemoteException;
}
