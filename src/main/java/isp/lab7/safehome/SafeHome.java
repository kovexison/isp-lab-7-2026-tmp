package isp.lab7.safehome;

public class SafeHome {

    public static void main(String[] args) {
        DoorLockController controller = new DoorLockController();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Select user type:");
            System.out.println("1 - Admin");
            System.out.println("2 - Tenant");
            System.out.println("0 - Exit");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runAdminMenu(controller, scanner);
                    break;
                case "2":
                    runTenantMenu(controller, scanner);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void runTenantMenu(DoorLockController controller, java.util.Scanner scanner) {
        System.out.println("Enter pin:");
        String pin = scanner.nextLine().trim();
        try {
            DoorStatus status = controller.enterPin(pin);
            System.out.println("Door status: " + status);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void runAdminMenu(DoorLockController controller, java.util.Scanner scanner) {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("Admin menu:");
            System.out.println("1 - Add tenant");
            System.out.println("2 - Remove tenant");
            System.out.println("3 - View access logs");
            System.out.println("0 - Back");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Tenant name:");
                    String name = scanner.nextLine().trim();
                    System.out.println("Tenant pin:");
                    String pin = scanner.nextLine().trim();
                    try {
                        controller.addTenant(pin, name);
                        System.out.println("Tenant added.");
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case "2":
                    System.out.println("Tenant name:");
                    String tenantName = scanner.nextLine().trim();
                    try {
                        controller.removeTenant(tenantName);
                        System.out.println("Tenant removed.");
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case "3":
                    java.util.List<AccessLog> logs = controller.getAccessLogs();
                    if (logs.isEmpty()) {
                        System.out.println("No access logs.");
                    } else {
                        for (AccessLog log : logs) {
                            System.out.println(log);
                        }
                    }
                    break;
                case "0":
                    adminRunning = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
