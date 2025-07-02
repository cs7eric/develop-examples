package cn.nooops.example_2;

import java.io.*;
import java.util.*;

/**
 * 员工层级处理打印
 *
 * @author cccs7 - csq020611@gmail.com
 * @date 2025/06/22
 */
public class EmployeeHierarchyPrinter {

    /**
     * 员工类，包含员工信息和直接下属列表
     */
    static class Employee {
        /**
         * 名字
         */
        String name;
        /**
         * ID
         */
        String id;
        /**
         * 年龄
         */
        int age;
        /**
         * 经理ID
         */
        String managerId;
        /**
         * 下属
         */
        List<Employee> subordinates = new ArrayList<>();

        public Employee(String name, String id, int age, String managerId) {
            this.name = name;
            this.id = id;
            this.age = age;
            this.managerId = managerId;
        }
    }

    public static void main(String[] args) {
        // 输入文件和输出文件路径
        String inputFile = "G:/project-repo/develop-examples/nooops-examples/examples_002/src/main/resources/exployees.txt";
        String outputFile = "hierarchy_output.txt";
        
        try {
            // 读取员工数据并构建组织结构
            Map<String, Employee> employees = readEmployeeData(inputFile);
            List<Employee> roots = buildHierarchy(employees);
            
            // 打印组织结构到文件
            printHierarchyToFile(roots, outputFile);
            
            System.out.println("组织结构已成功生成到: " + outputFile);
        } catch (IOException e) {
            System.err.println("处理文件时出错: " + e.getMessage());
        }
    }

    /**
     * 从文件读取员工数据
     * @param filename 输入文件名
     * @return 员工ID到员工对象的映射
     * @throws IOException 如果读取文件失败
     */
    private static Map<String, Employee> readEmployeeData(String filename) throws IOException {
        Map<String, Employee> employees = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 解析每行数据
                String[] parts = line.split(",\\s*");
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    String id = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    String managerId = parts.length > 3 ? parts[3].trim() : null;
                    
                    // 创建员工对象并添加到映射
                    employees.put(id, new Employee(name, id, age, managerId));
                }
            }
        }
        return employees;
    }

    /**
     * 构建层级关系结构
     * @param employees 所有员工的映射
     * @return 顶级领导列表(没有上级的员工)
     */
    private static List<Employee> buildHierarchy(Map<String, Employee> employees) {
        List<Employee> roots = new ArrayList<>();
        
        // 建立上下级关系
        for (Employee employee : employees.values()) {
            if (employee.managerId != null && !employee.managerId.isEmpty()) {
                Employee manager = employees.get(employee.managerId);
                if (manager != null) {
                    manager.subordinates.add(employee);
                }
            }
        }
        
        // 找出所有root领导
        for (Employee employee : employees.values()) {
            if (employee.managerId == null || employee.managerId.isEmpty() 
                || !employees.containsKey(employee.managerId)) {
                roots.add(employee);
            }
        }
        
        // 按ID排序顶级领导
        roots.sort(Comparator.comparing(e -> e.id));
        return roots;
    }

    /**
     * 将层级结构打印到文件
     * @param roots 顶级领导列表
     * @param filename 输出文件名
     * @throws IOException 如果写入文件失败
     */
    private static void printHierarchyToFile(List<Employee> roots, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Employee root : roots) {
                printEmployee(root, writer, 0);
            }
        }
    }

    /**
     * 递归打印员工及其下属
     * @param employee 当前员工
     * @param writer 输出写入器
     * @param level 当前层级(用于缩进)
     */
    private static void printEmployee(Employee employee, PrintWriter writer, int level) {
        // 根据层级生成缩进(每级缩进4个空格)
        String indent = "    ".repeat(level);
        
        // 打印员工信息
        writer.println(indent + employee.name + " (ID:" + employee.id + ", 年龄:" + employee.age + ")");
        
        // 递归打印所有下属
        for (Employee subordinate : employee.subordinates) {
            printEmployee(subordinate, writer, level + 1);
        }
    }
}