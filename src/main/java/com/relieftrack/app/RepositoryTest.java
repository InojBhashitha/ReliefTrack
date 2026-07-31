package com.relieftrack.app;

import com.relieftrack.enums.Category;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.repository.ReliefItemRepository;

import java.time.LocalDate;

public class RepositoryTest {

    public static void main(String[] args) throws Exception {

        ReliefItemRepository repository = new ReliefItemRepository();

        repository.save(
                new ReliefItem(
                        0,
                        "Drinking Water",
                        Category.WATER,
                        LocalDate.of(2027, 12, 31)
                )
        );

        repository.save(
                new ReliefItem(
                        0,
                        "Rice",
                        Category.FOOD,
                        LocalDate.of(2028, 6, 30)
                )
        );

        repository.findAll().forEach(System.out::println);

    }
}