package ru.otus.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agreement {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return "Agreement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
