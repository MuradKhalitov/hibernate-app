package ru.murad;

import ru.murad.dao.UserDao;
import ru.murad.model.User;
import ru.murad.service.impl.UserServiceImpl;
import ru.murad.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        UserService userService = new UserServiceImpl(new UserDao());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createUser(scanner, userService);
                case "2" -> getUserById(scanner, userService);
                case "3" -> getAllUsers(userService);
                case "4" -> updateUser(scanner, userService);
                case "5" -> deleteUser(scanner, userService);
                case "0" -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный пункт меню.");
            }

            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("============== USER SERVICE ==============");
        System.out.println("1 — Создать пользователя");
        System.out.println("2 — Найти пользователя по ID");
        System.out.println("3 — Показать всех пользователей");
        System.out.println("4 — Обновить пользователя");
        System.out.println("5 — Удалить пользователя");
        System.out.println("0 — Выход");
        System.out.println("==========================================");
    }

    private static void createUser(Scanner sc, UserService service) {
        System.out.print("Введите имя: ");
        String name = sc.nextLine();

        System.out.print("Введите email: ");
        String email = sc.nextLine();

        System.out.print("Введите возраст: ");
        int age = readInt(sc);

        try {
            User user = service.createUser(name, email, age);
            System.out.println("Создан пользователь: " + user);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void getUserById(Scanner sc, UserService service) {
        System.out.print("Введите ID: ");
        long id = readLong(sc);

        Optional<User> user = service.getUserById(id);

        if (user.isPresent()) {
            System.out.println("Найден: " + user.get());
        } else {
            System.out.println("Пользователь не найден.");
        }
    }

    private static void getAllUsers(UserService service) {
        List<User> users = service.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("Пользователи отсутствуют.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void updateUser(Scanner sc, UserService service) {
        System.out.print("Введите ID пользователя: ");
        long id = readLong(sc);

        System.out.print("Новое имя: ");
        String name = sc.nextLine();

        System.out.print("Новый email: ");
        String email = sc.nextLine();

        System.out.print("Новый возраст: ");
        int age = readInt(sc);

        try {
            User updated = service.updateUser(id, name, email, age);
            System.out.println("Обновлено: " + updated);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteUser(Scanner sc, UserService service) {
        System.out.print("Введите ID: ");
        long id = readLong(sc);

        try {
            boolean ok = service.deleteUser(id);
            System.out.println(ok ? "Удалён успешно." : "Пользователь не найден.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static int readInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }

    private static long readLong(Scanner sc) {
        while (true) {
            try {
                return Long.parseLong(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }
}

