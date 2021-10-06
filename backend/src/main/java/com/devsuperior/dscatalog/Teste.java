package com.devsuperior.dscatalog;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Teste {
    public static void main(String[] args) {
        System.out.println(gerarPossibilidades(6));
    }

    private static List<Integer> gerarPossibilidades(Integer maxDigit) {
        return IntStream.rangeClosed(1000, 10000).boxed().filter(i -> {
            List<Integer> characters = String.valueOf(i)
                    .chars()
                    .mapToObj(e -> Integer.parseInt(Character.toString(e))).collect(Collectors.toList());
            return (characters.stream().allMatch(it -> it <= maxDigit) && characters.stream().reduce(0, Integer::sum) == 21);
        }).collect(Collectors.toList());
    }
}
