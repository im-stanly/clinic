package com.Clinic.WebApp.repository;

import com.Clinic.WebApp.exception.NotFoundException;
import com.Clinic.WebApp.model.PersonsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepository implements RepoInterface {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final String GET_PERSON_PROPERTIES_SQL = "SELECT id, first_name, last_name, address, city, " +
            "telephone, email, fav_animal FROM Persons";

    public List<PersonsModel> getPersons(){
        return jdbcTemplate.query(GET_PERSON_PROPERTIES_SQL + " LIMIT 20",
                BeanPropertyRowMapper.newInstance(PersonsModel.class));
    }
    public PersonsModel getById(int id){
        return getPersonsByKind("id", id).get(0);
    }
    public List<PersonsModel> getByName(String name){
        return getPersonsByKind("first_name", name);
    }

    public int save(List<PersonsModel> persons){
        persons.forEach( singlePer ->
            jdbcTemplate.update(
                    "INSERT INTO Persons(first_name, last_name, address, city, telephone, email, fav_animal) " +
                            "VALUES(?, ?, ?, ?, ?, ?, ?)",
                    singlePer.getFirst_name(), singlePer.getLast_name(), singlePer.getAddress(),
                    singlePer.getCity(), singlePer.getTelephone(), singlePer.getEmail(), singlePer.getFavAnimal()
            ));
        return 202;
    }

    public int update(int oldId, PersonsModel person){
        return jdbcTemplate.update(
                "UPDATE Persons SET first_name = ?, last_name = ?, address = ?, city = ?, telephone = ?, " +
                        "email = ?, fav_animal = ? WHERE id=?",
                person.getFirst_name(), person.getLast_name(), person.getAddress(), person.getCity(),
                person.getTelephone(), person.getEmail(), person.getFavAnimal(), oldId) > 0 ? 202 : 418;
    }

    public int delete(int id){
        if (!isElementOfLibrary(jdbcTemplate,"Persons", "id", id))
            throw new NotFoundException("Person", id);
        return jdbcTemplate.update("DELETE FROM Persons WHERE id = " + id) > 0 ? 202 : 418;
    }

    private List<PersonsModel> getPersonsByKind(String kind, Object object){
        List<PersonsModel> persons = jdbcTemplate.query(GET_PERSON_PROPERTIES_SQL + " WHERE "
                + kind + "=?", BeanPropertyRowMapper.newInstance(PersonsModel.class), object);

        if (persons == null || persons.isEmpty()){
            if (object.getClass().equals(String.class)) {
                throw new NotFoundException("Person", (String) object);
            }
            else {
                throw new NotFoundException("Person", (Integer) object);
            }
        }
        return persons;
    }
}
