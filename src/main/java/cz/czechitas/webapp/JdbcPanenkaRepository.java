package cz.czechitas.webapp;

import java.sql.*;
import java.util.*;
import org.mariadb.jdbc.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.lookup.*;
import org.springframework.jdbc.support.*;

public class JdbcPanenkaRepository implements PanenkaRepository {

    JdbcTemplate odesilacDotazu;
    BeanPropertyRowMapper<Panenka> prevodnik;

    public JdbcPanenkaRepository() {

        try {
            MariaDbDataSource konfiguraceDatabaze = new MariaDbDataSource();
            konfiguraceDatabaze.setUserName("student");
            konfiguraceDatabaze.setPassword("password");
            konfiguraceDatabaze.setUrl("jdbc:mariadb://localhost:3306/SkladPanenek");

            odesilacDotazu = new JdbcTemplate(konfiguraceDatabaze);
            prevodnik = new BeanPropertyRowMapper<>(Panenka.class);
        } catch (SQLException e) {
            throw new DataSourceLookupFailureException("Nepodarilo se vytvorit DataSource", e);
        }
    }

    @Override
    public List<Panenka> findAll() {
        List<Panenka> vsechnyPanenky = odesilacDotazu.query("SELECT ID, Jmeno, Vrsek, Spodek, CasVzniku FROM " +
                "Panenky ORDER BY CasVzniku DESC", prevodnik);
        return vsechnyPanenky;
    }

    @Override
    public Panenka findById(Long id) {
        Panenka ulozenaPanenka = odesilacDotazu.queryForObject("SELECT ID, Jmeno, Vrsek, Spodek, CasVzniku FROM Panenky WHERE ID = ?",
                prevodnik,
                id);
        return ulozenaPanenka;
    }

    @Override
    public Panenka save(Panenka zaznamKUlozeni) {
        Panenka novaPanenka = new Panenka(zaznamKUlozeni.getJmeno(), zaznamKUlozeni.getVrsek(),
                zaznamKUlozeni.getSpodek());
        if (zaznamKUlozeni.getId() != null) {
            throw new IllegalArgumentException("Panenka.ID musí být null. Panenku lze do databáze jen přidat, nikoliv měnit.");
        }
        Panenka novyZaznam = clone(novaPanenka);
        GeneratedKeyHolder drzakNaVygenerovanyKlic = new GeneratedKeyHolder();
        String sql ="INSERT INTO Panenky (Jmeno, Vrsek, Spodek, CasVzniku) " +
                "VALUES (?, ?, ?, ?)";
        odesilacDotazu.update((Connection con) -> {
                    PreparedStatement prikaz = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    prikaz.setString(1, novyZaznam.getJmeno());
                    prikaz.setString(2, novyZaznam.getVrsek());
                    prikaz.setString(3, novyZaznam.getSpodek());
                    prikaz.setTimestamp(4, new Timestamp(novyZaznam.getCasVzniku().toEpochMilli()));
                    return prikaz;
                },
                drzakNaVygenerovanyKlic);
        novyZaznam.setId(drzakNaVygenerovanyKlic.getKey().longValue());
        return novyZaznam;
    }

    @Override
    public void delete(Long id) {
        odesilacDotazu.update("DELETE FROM Panenky WHERE ID = ?",
                id);
    }

    public Panenka updatuj(Panenka zaznamKUlozeni) {
        zaznamKUlozeni = clone(zaznamKUlozeni);
        odesilacDotazu.update("UPDATE Panenky SET Jmeno = ?, Vrsek = ?, spodek = ? WHERE ID = ?",
                zaznamKUlozeni.getJmeno(),
                zaznamKUlozeni.getVrsek(),
                zaznamKUlozeni.getSpodek(),
                zaznamKUlozeni.getId());
        return zaznamKUlozeni;
    }

    private Panenka clone(Panenka puvodniObjekt) {
        return new Panenka(puvodniObjekt.getId(), puvodniObjekt.getJmeno(), puvodniObjekt.getVrsek(), puvodniObjekt.getSpodek(), puvodniObjekt.getCasVzniku());
    }

}

