package ui;

import excecao.DadosInvalidosException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

final class Ui {

    static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Ui() {
    }

    static void erro(Component pai, Exception e) {
        JOptionPane.showMessageDialog(pai, e.getMessage(), "Não foi possível concluir", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, msg, "Clínica", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirmar(Component pai, String msg) {
        return JOptionPane.showConfirmDialog(pai, msg, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    static LocalDate lerData(String texto) throws DadosInvalidosException {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new DadosInvalidosException("Data inválida: \"" + texto + "\". Use o formato dd/mm/aaaa, ex.: "
                    + LocalDate.now().plusDays(1).format(FORMATO_DATA));
        }
    }

    static String dinheiro(double v) {
        return String.format("R$ %.2f", v);
    }

    static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    static JTable tabela(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setRowHeight(24);
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }
}
