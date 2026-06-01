// SCADASolar.java - VERSÃO FINAL CORRIGIDA
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class SCADASolar {

    // ==================== CONSTANTES DE COR ====================
    static final Color COR_FUNDO     = new Color(15, 20, 30);
    static final Color COR_PAINEL    = new Color(22, 30, 45);
    static final Color COR_PAINEL2   = new Color(28, 38, 55);
    static final Color COR_DESTAQUE  = new Color(0, 180, 255);
    static final Color COR_VERDE     = new Color(0, 220, 120);
    static final Color COR_AMARELO   = new Color(255, 200, 0);
    static final Color COR_VERMELHO  = new Color(255, 80, 80);
    static final Color COR_TEXTO     = new Color(220, 230, 245);
    static final Color COR_TEXTO_SEC = new Color(140, 160, 190);
    static final Color COR_LARANJA   = new Color(255, 140, 0);
    static final Color COR_GRADE    = new Color(35, 50, 70);

    // ==================== MODELOS ====================
    static class UsinaFotovoltaica {
        String id, nome, localizacao, responsavel, status;
        double capacidadeKwp, areaM2;
        int anoInstalacao;

        UsinaFotovoltaica(String id, String nome, String loc, double cap,
                          double area, int ano, String resp, String status) {
            this.id=id; this.nome=nome; this.localizacao=loc;
            this.capacidadeKwp=cap; this.areaM2=area;
            this.anoInstalacao=ano; this.responsavel=resp; this.status=status;
        }

        String toCsv() {
            return id+","+nome+","+localizacao+","+capacidadeKwp+","+
                   areaM2+","+anoInstalacao+","+responsavel+","+status;
        }

        static UsinaFotovoltaica fromCsv(String line) {
            String[] p = line.split(",");
            return new UsinaFotovoltaica(p[0],p[1],p[2],
                Double.parseDouble(p[3]),Double.parseDouble(p[4]),
                Integer.parseInt(p[5]),p[6],p[7]);
        }
    }

    static class RegistroProd {
        String usinaId;
        LocalDate data;
        double energiaKwh, irradiancia, tempMedia, pr;

        RegistroProd(String uid, LocalDate d, double e,
                     double ir, double t, double pr) {
            this.usinaId=uid; this.data=d; this.energiaKwh=e;
            this.irradiancia=ir; this.tempMedia=t; this.pr=pr;
        }

        String toCsv() {
            return usinaId+","+data+","+energiaKwh+","+
                   irradiancia+","+tempMedia+","+pr;
        }

        static RegistroProd fromCsv(String line) {
            String[] p = line.split(",");
            return new RegistroProd(p[0],LocalDate.parse(p[1]),
                Double.parseDouble(p[2]),Double.parseDouble(p[3]),
                Double.parseDouble(p[4]),Double.parseDouble(p[5]));
        }
    }

    static class Evento {
        String id, usinaId, tipo, descricao, severidade, status;
        LocalDateTime dataHora;

        Evento(String id, String uid, LocalDateTime dt, String tipo,
               String desc, String sev, String status) {
            this.id=id; this.usinaId=uid; this.dataHora=dt;
            this.tipo=tipo; this.descricao=desc;
            this.severidade=sev; this.status=status;
        }

        String toCsv() {
            return id+","+usinaId+","+dataHora+","+tipo+","+
                   descricao+","+severidade+","+status;
        }

        static Evento fromCsv(String line) {
            String[] p = line.split(",",7);
            return new Evento(p[0],p[1],LocalDateTime.parse(p[2]),
                p[3],p[4],p[5],p[6]);
        }
    }

    // ==================== EQUIPAMENTOS ====================
    static abstract class Equipamento {
        String id, nome, usinaId, status, dataInstalacao;
        abstract String getTipo();
        abstract String toCsv();
    }

    static class Inversor extends Equipamento {
        double potenciaNominal, eficiencia;
        String modelo, fabricante;

        Inversor(String id, String nome, String uid, double pot,
                 double ef, String mod, String fab, String status, String data) {
            this.id=id; this.nome=nome; this.usinaId=uid;
            this.potenciaNominal=pot; this.eficiencia=ef;
            this.modelo=mod; this.fabricante=fab;
            this.status=status; this.dataInstalacao=data;
        }

        @Override public String getTipo() { return "INVERSOR"; }

        @Override public String toCsv() {
            return "INVERSOR,"+id+","+nome+","+usinaId+","+potenciaNominal+
                   ","+eficiencia+","+modelo+","+fabricante+","+status+
                   ","+dataInstalacao;
        }

        static Inversor fromCsv(String[] p) {
            return new Inversor(p[1],p[2],p[3],
                Double.parseDouble(p[4]),Double.parseDouble(p[5]),
                p[6],p[7],p[8],p[9]);
        }
    }

    static class StringSolar extends Equipamento {
        int numModulos;
        double tensaoNominal, correnteNominal;

        StringSolar(String id, String nome, String uid, int nMod,
                    double tensao, double corrente, String status, String data) {
            this.id=id; this.nome=nome; this.usinaId=uid;
            this.numModulos=nMod; this.tensaoNominal=tensao;
            this.correnteNominal=corrente; this.status=status;
            this.dataInstalacao=data;
        }

        @Override public String getTipo() { return "STRING"; }

        @Override public String toCsv() {
            return "STRING,"+id+","+nome+","+usinaId+","+numModulos+
                   ","+tensaoNominal+","+correnteNominal+","+status+
                   ","+dataInstalacao;
        }

        static StringSolar fromCsv(String[] p) {
            return new StringSolar(p[1],p[2],p[3],Integer.parseInt(p[4]),
                Double.parseDouble(p[5]),Double.parseDouble(p[6]),p[7],p[8]);
        }
    }

    static class ModuloFotovoltaico extends Equipamento {
        double potenciaPico, eficiencia;
        String tecnologia, fabricante;

        ModuloFotovoltaico(String id, String nome, String uid, double pot,
                           double ef, String tec, String fab,
                           String status, String data) {
            this.id=id; this.nome=nome; this.usinaId=uid;
            this.potenciaPico=pot; this.eficiencia=ef;
            this.tecnologia=tec; this.fabricante=fab;
            this.status=status; this.dataInstalacao=data;
        }

        @Override public String getTipo() { return "MODULO"; }

        @Override public String toCsv() {
            return "MODULO,"+id+","+nome+","+usinaId+","+potenciaPico+
                   ","+eficiencia+","+tecnologia+","+fabricante+","+status+
                   ","+dataInstalacao;
        }

        static ModuloFotovoltaico fromCsv(String[] p) {
            return new ModuloFotovoltaico(p[1],p[2],p[3],
                Double.parseDouble(p[4]),Double.parseDouble(p[5]),
                p[6],p[7],p[8],p[9]);
        }
    }

    static class MedidorBidirecional extends Equipamento {
        double capacidadeKw;
        String protocolo, numeroSerie;

        MedidorBidirecional(String id, String nome, String uid, double cap,
                            String prot, String serie, String status, String data) {
            this.id=id; this.nome=nome; this.usinaId=uid;
            this.capacidadeKw=cap; this.protocolo=prot;
            this.numeroSerie=serie; this.status=status;
            this.dataInstalacao=data;
        }

        @Override public String getTipo() { return "MEDIDOR"; }

        @Override public String toCsv() {
            return "MEDIDOR,"+id+","+nome+","+usinaId+","+capacidadeKw+
                   ","+protocolo+","+numeroSerie+","+status+","+dataInstalacao;
        }

        static MedidorBidirecional fromCsv(String[] p) {
            return new MedidorBidirecional(p[1],p[2],p[3],
                Double.parseDouble(p[4]),p[5],p[6],p[7],p[8]);
        }
    }

    // ==================== BANCO DE DADOS (SINGLETON) ====================
    static class BancoDados {
        private static BancoDados instancia;
        private static final String PASTA          = "scada_dados";
        private static final String ARQ_USUARIOS   = PASTA+"/usuarios.csv";
        private static final String ARQ_USINAS     = PASTA+"/usinas.csv";
        private static final String ARQ_EQUIP      = PASTA+"/equipamentos.csv";
        private static final String ARQ_PRODUCAO   = PASTA+"/producao.csv";
        private static final String ARQ_EVENTOS    = PASTA+"/eventos.csv";

        Map<String,String[]> usuarios    = new LinkedHashMap<>();
        List<UsinaFotovoltaica> usinas   = new ArrayList<>();
        List<Equipamento> equipamentos   = new ArrayList<>();
        List<RegistroProd> producao      = new ArrayList<>();
        List<Evento> eventos             = new ArrayList<>();

        private BancoDados() {
            new File(PASTA).mkdirs();
            carregarTudo();
        }

        static BancoDados get() {
            if (instancia == null) instancia = new BancoDados();
            return instancia;
        }

        void carregarTudo() {
            carregarUsuarios(); carregarUsinas();
            carregarEquipamentos(); carregarProducao(); carregarEventos();
            if (usinas.isEmpty()) carregarDadosIniciais();
        }

        void carregarUsuarios() {
            File f = new File(ARQ_USUARIOS);
            if (!f.exists()) {
                usuarios.put("admin",       new String[]{"admin123","MASTER"});
                usuarios.put("operador",    new String[]{"op2024","OPERADOR"});
                usuarios.put("visualizador",new String[]{"vis123","VISUALIZADOR"});
                salvarUsuarios(); return;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",",3);
                    if (p.length >= 3) usuarios.put(p[0], new String[]{p[1],p[2]});
                }
            } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarUsuarios() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQ_USUARIOS))) {
                for (Map.Entry<String,String[]> e : usuarios.entrySet())
                    pw.println(e.getKey()+","+e.getValue()[0]+","+e.getValue()[1]);
            } catch (IOException e) { e.printStackTrace(); }
        }

        void carregarUsinas() {
            File f = new File(ARQ_USINAS);
            if (!f.exists()) return;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null)
                    if (!line.trim().isEmpty())
                        usinas.add(UsinaFotovoltaica.fromCsv(line.trim()));
            } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarUsinas() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQ_USINAS))) {
                for (UsinaFotovoltaica u : usinas) pw.println(u.toCsv());
            } catch (IOException e) { e.printStackTrace(); }
        }

        void carregarEquipamentos() {
            File f = new File(ARQ_EQUIP);
            if (!f.exists()) return;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    switch (p[0]) {
                        case "INVERSOR": if(p.length>=10) equipamentos.add(Inversor.fromCsv(p)); break;
                        case "STRING":   if(p.length>=9)  equipamentos.add(StringSolar.fromCsv(p)); break;
                        case "MODULO":   if(p.length>=10) equipamentos.add(ModuloFotovoltaico.fromCsv(p)); break;
                        case "MEDIDOR":  if(p.length>=9)  equipamentos.add(MedidorBidirecional.fromCsv(p)); break;
                    }
                }
            } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarEquipamentos() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQ_EQUIP))) {
                for (Equipamento eq : equipamentos) pw.println(eq.toCsv());
            } catch (IOException e) { e.printStackTrace(); }
        }

        void carregarProducao() {
            File f = new File(ARQ_PRODUCAO);
            if (!f.exists()) return;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null)
                    if (!line.trim().isEmpty())
                        producao.add(RegistroProd.fromCsv(line.trim()));
            } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarProducao() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQ_PRODUCAO))) {
                for (RegistroProd r : producao) pw.println(r.toCsv());
            } catch (IOException e) { e.printStackTrace(); }
        }

        void carregarEventos() {
            File f = new File(ARQ_EVENTOS);
            if (!f.exists()) return;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null)
                    if (!line.trim().isEmpty())
                        eventos.add(Evento.fromCsv(line.trim()));
            } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarEventos() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQ_EVENTOS))) {
                for (Evento ev : eventos) pw.println(ev.toCsv());
            } catch (IOException e) { e.printStackTrace(); }
        }

        void carregarDadosIniciais() {
            usinas.add(new UsinaFotovoltaica("U001","Usina Nordeste Solar",
                "Petrolina-PE",500,3500,2019,"Carlos Silva","OPERACIONAL"));
            usinas.add(new UsinaFotovoltaica("U002","Usina Centro-Oeste",
                "Cuiabá-MT",350,2450,2020,"Ana Costa","OPERACIONAL"));
            usinas.add(new UsinaFotovoltaica("U003","Usina Sudeste",
                "Ribeirão Preto-SP",200,1400,2021,"Pedro Alves","OPERACIONAL"));
            usinas.add(new UsinaFotovoltaica("U004","Usina Sul",
                "Passo Fundo-RS",150,1050,2022,"Maria Santos","MANUTENCAO"));
            salvarUsinas();

            // Inversores
            String[][] invs = {
                {"I001","Inversor NE-1","U001","100","97.5","SC 100","SMA"},
                {"I002","Inversor NE-2","U001","100","97.5","SC 100","SMA"},
                {"I003","Inversor NE-3","U001","100","97.2","SC 100","SMA"},
                {"I004","Inversor CO-1","U002","75","97.8","Symo 75","Fronius"},
                {"I005","Inversor CO-2","U002","75","97.8","Symo 75","Fronius"},
                {"I006","Inversor SE-1","U003","50","97.0","PVS-50","ABB"},
                {"I007","Inversor SE-2","U003","50","97.0","PVS-50","ABB"},
                {"I008","Inversor S-1","U004","40","97.6","SUN2000","Huawei"},
                {"I009","Inversor S-2","U004","40","97.6","SUN2000","Huawei"},
                {"I010","Inversor NE-4","U001","75","96.8","MAX75KTL","Growatt"},
                {"I011","Inversor CO-3","U002","60","97.5","STripower","SMA"},
                {"I012","Inversor SE-3","U003","55","97.9","Eco 55","Fronius"}
            };
            for (String[] v : invs)
                equipamentos.add(new Inversor(v[0],v[1],v[2],
                    Double.parseDouble(v[3]),Double.parseDouble(v[4]),
                    v[5],v[6],"OPERACIONAL","2022-01-15"));

            // Módulos
            String[][] mods = {
                {"M001","Array NE-A","U001","500","21.3","Mono","JA Solar"},
                {"M002","Array NE-B","U001","500","21.5","Mono","LONGi"},
                {"M003","Array CO-A","U002","400","20.8","Poli","Canadian"},
                {"M004","Array CO-B","U002","400","21.0","Mono","JA Solar"},
                {"M005","Array SE-A","U003","250","21.2","Mono","LONGi"},
                {"M006","Array S-A","U004","200","20.5","Poli","Trina"}
            };
            for (String[] v : mods)
                equipamentos.add(new ModuloFotovoltaico(v[0],v[1],v[2],
                    Double.parseDouble(v[3]),Double.parseDouble(v[4]),
                    v[5],v[6],"OPERACIONAL","2022-01-10"));

            // Strings
            String[][] strs = {
                {"S001","String Box NE-1","U001","20","600","9.8"},
                {"S002","String Box NE-2","U001","20","600","9.8"},
                {"S003","String Box CO-1","U002","18","580","9.5"},
                {"S004","String Box CO-2","U002","18","580","9.5"},
                {"S005","String Box SE-1","U003","15","560","9.2"},
                {"S006","String Box S-1","U004","12","550","8.8"},
                {"S007","String Box S-2","U004","12","550","8.8"}
            };
            for (String[] v : strs)
                equipamentos.add(new StringSolar(v[0],v[1],v[2],
                    Integer.parseInt(v[3]),Double.parseDouble(v[4]),
                    Double.parseDouble(v[5]),"OPERACIONAL","2022-01-12"));

            // Medidores
            String[][] meds = {
                {"MD001","Medidor NE","U001","600","Modbus RTU","SN-NE-001"},
                {"MD002","Medidor CO","U002","450","Modbus TCP","SN-CO-002"},
                {"MD003","Medidor SE","U003","300","DNP3","SN-SE-003"},
                {"MD004","Medidor S","U004","200","IEC 61850","SN-S-004"}
            };
            for (String[] v : meds)
                equipamentos.add(new MedidorBidirecional(v[0],v[1],v[2],
                    Double.parseDouble(v[3]),v[4],v[5],
                    "OPERACIONAL","2022-01-20"));

            salvarEquipamentos();
            gerarProducaoSimulada();
            gerarEventosIniciais();
        }

        void gerarProducaoSimulada() {
            Random rnd = new Random(42);
            double[] capKwp    = {500, 350, 200, 150};
            double[] irradBase = {6.2, 5.8, 5.1, 4.5};
            String[] ids = {"U001","U002","U003","U004"};

            for (int u = 0; u < 4; u++) {
                for (int d = 89; d >= 0; d--) {
                    LocalDate data = LocalDate.now().minusDays(d);
                    double sazo = 1 + 0.25 * Math.sin(
                        (data.getDayOfYear()-80)*2*Math.PI/365);
                    double irr  = irradBase[u] * sazo * (0.85 + rnd.nextDouble()*0.30);
                    double temp = 28 + 10*sazo + rnd.nextDouble()*8 - 4;
                    double perda = 1 - 0.004 * Math.max(0, temp-25);
                    double pr   = (0.76 + rnd.nextDouble()*0.06) * perda;
                    double energia = capKwp[u] * irr * pr
                                   * (0.92 + rnd.nextDouble()*0.08);
                    producao.add(new RegistroProd(ids[u], data,
                        Math.round(energia*10.0)/10.0,
                        Math.round(irr*100.0)/100.0,
                        Math.round(temp*10.0)/10.0,
                        Math.round(pr*1000.0)/1000.0));
                }
            }
            salvarProducao();
        }

        void gerarEventosIniciais() {
            LocalDateTime base = LocalDateTime.now();
            String[][] evts = {
                {"E001","U001","FALHA","Sobrecorrente Inversor NE-2","CRITICO","ATIVO"},
                {"E002","U001","ALERTA","Temperatura elevada módulos NE-A","ALTO","ATIVO"},
                {"E003","U002","FALHA","Comunicação perdida Medidor CO","ALTO","RESOLVIDO"},
                {"E004","U002","ALERTA","PR abaixo de 75%","MEDIO","ATIVO"},
                {"E005","U003","INFO","Manutenção preventiva realizada","BAIXO","RESOLVIDO"},
                {"E006","U003","ALERTA","String SE-1 corrente reduzida","MEDIO","ATIVO"},
                {"E007","U004","FALHA","Inversor S-1 offline","CRITICO","ATIVO"},
                {"E008","U004","ALERTA","Sombreamento detectado","MEDIO","RESOLVIDO"},
                {"E009","U001","INFO","Limpeza painéis realizada","BAIXO","RESOLVIDO"},
                {"E010","U002","ALERTA","Fusível string CO-2 queimado","ALTO","ATIVO"},
                {"E011","U003","INFO","Atualização firmware concluída","BAIXO","RESOLVIDO"},
                {"E012","U004","ALERTA","Geração 20% abaixo esperado","ALTO","ATIVO"}
            };
            for (int i = 0; i < evts.length; i++) {
                String[] e = evts[i];
                eventos.add(new Evento(e[0],e[1],
                    base.minusDays(i*3L).minusHours(i*2L),
                    e[2],e[3],e[4],e[5]));
            }
            salvarEventos();
        }

        String[] autenticar(String user, String senha) {
            String[] d = usuarios.get(user);
            return (d != null && d[0].equals(senha)) ? d : null;
        }
    }

    // ==================== COMPONENTES UI ====================
    static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius, thickness;

        RoundBorder(Color c, int r, int t) {
            this.color=c; this.radius=r; this.thickness=t;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x+1,y+1,w-2,h-2,radius,radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness+4,thickness+8,thickness+4,thickness+8);
        }
    }

    static class Fab {
        static JLabel titulo(String txt, int size, Color cor) {
            JLabel l = new JLabel(txt);
            l.setFont(new Font("Segoe UI",Font.BOLD,size));
            l.setForeground(cor);
            return l;
        }

        static JLabel label(String txt) {
            JLabel l = new JLabel(txt);
            l.setFont(new Font("Segoe UI",Font.PLAIN,13));
            l.setForeground(COR_TEXTO);
            return l;
        }

        static JTextField campo(String placeholder) {
            JTextField tf = new JTextField(placeholder,20) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(COR_PAINEL2);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            tf.setOpaque(false);
            tf.setForeground(COR_TEXTO);
            tf.setCaretColor(COR_DESTAQUE);
            tf.setBorder(new RoundBorder(COR_DESTAQUE,10,1));
            tf.setFont(new Font("Segoe UI",Font.PLAIN,13));
            return tf;
        }

        static JPasswordField senha() {
            JPasswordField pf = new JPasswordField(20);
            pf.setOpaque(false);
            pf.setBackground(COR_PAINEL2);
            pf.setForeground(COR_TEXTO);
            pf.setCaretColor(COR_DESTAQUE);
            pf.setBorder(new RoundBorder(COR_DESTAQUE,10,1));
            pf.setFont(new Font("Segoe UI",Font.PLAIN,13));
            return pf;
        }

        static JButton botao(String txt, Color cor) {
            JButton b = new JButton(txt) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    Color base = getModel().isPressed()   ? cor.darker().darker() :
                                 getModel().isRollover()  ? cor.brighter() : cor;
                    g2.setColor(base);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                    g2.setColor(Color.WHITE);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (getWidth()-fm.stringWidth(getText()))/2;
                    int ty = (getHeight()+fm.getAscent()-fm.getDescent())/2;
                    g2.drawString(getText(),tx,ty);
                    g2.dispose();
                }
            };
            b.setFont(new Font("Segoe UI",Font.BOLD,13));
            b.setForeground(Color.WHITE);
            b.setPreferredSize(new Dimension(160,40));
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        static JComboBox<String> combo(String[] ops) {
            JComboBox<String> cb = new JComboBox<>(ops);
            cb.setBackground(COR_PAINEL2);
            cb.setForeground(COR_TEXTO);
            cb.setFont(new Font("Segoe UI",Font.PLAIN,13));
            cb.setBorder(new RoundBorder(COR_DESTAQUE,8,1));
            return cb;
        }

        static JPanel painel(Color fundo) {
            JPanel p = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(fundo);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                    g2.dispose();
                }
            };
            p.setOpaque(false);
            return p;
        }
    }

    // ==================== GRÁFICO DE LINHAS ====================
    static class GraficoLinha extends JPanel {
        private List<double[]> series   = new ArrayList<>();
        private List<Color>    cores    = new ArrayList<>();
        private List<String>   legendas = new ArrayList<>();
        private String titulo, eixoX;
        private List<String> labelsX   = new ArrayList<>();

        // CORRIGIDO: removido eixoY não usado
        GraficoLinha(String titulo, String eixoX) {
            this.titulo=titulo; this.eixoX=eixoX;
            setBackground(COR_PAINEL);
            setPreferredSize(new Dimension(600,300));
        }

        void addSerie(double[] dados, Color cor, String legenda) {
            series.add(dados); cores.add(cor); legendas.add(legenda);
        }

        void setLabelsX(List<String> lbs) { this.labelsX=lbs; }
        void limpar() { series.clear(); cores.clear(); legendas.clear(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int W=getWidth(), H=getHeight();
            int ml=65, mr=20, mt=45, mb=50;
            int pw=W-ml-mr, ph=H-mt-mb;

            g2.setColor(COR_PAINEL);
            g2.fillRect(0,0,W,H);

            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Segoe UI",Font.BOLD,14));
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(titulo,(W-fmT.stringWidth(titulo))/2,25);

            if (series.isEmpty()) { g2.dispose(); return; }

            double maxV = 1;
            for (double[] s : series)
                for (double v : s) if (v > maxV) maxV = v;
            maxV = Math.ceil(maxV/100.0)*100;

            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for (int i = 0; i <= 5; i++) {
                double v = maxV*i/5.0;
                int y = mt+ph-(int)(ph*v/maxV);
                g2.setColor(COR_GRADE);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawLine(ml,y,ml+pw,y);
                g2.setColor(COR_TEXTO_SEC);
                String lbl = String.format("%.0f",v);
                g2.drawString(lbl, ml-g2.getFontMetrics().stringWidth(lbl)-5, y+4);
            }

            for (int s = 0; s < series.size(); s++) {
                double[] dados = series.get(s);
                if (dados.length < 2) continue;
                g2.setColor(cores.get(s));
                g2.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,
                                              BasicStroke.JOIN_ROUND));
                int n = dados.length;
                int[] xs = new int[n], ys = new int[n];
                for (int i = 0; i < n; i++) {
                    xs[i] = ml+(int)(pw*i/(n-1));
                    ys[i] = mt+ph-(int)(ph*dados[i]/maxV);
                }
                for (int i = 0; i < n-1; i++)
                    g2.drawLine(xs[i],ys[i],xs[i+1],ys[i+1]);
                for (int i = 0; i < n; i++)
                    g2.fillOval(xs[i]-3,ys[i]-3,6,6);
            }

            if (!labelsX.isEmpty()) {
                g2.setColor(COR_TEXTO_SEC);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
                int step = Math.max(1,labelsX.size()/7);
                for (int i = 0; i < labelsX.size(); i += step) {
                    int x = ml+(int)(pw*i/Math.max(1,labelsX.size()-1));
                    String lbl = labelsX.get(i);
                    int sw = g2.getFontMetrics().stringWidth(lbl);
                    g2.drawString(lbl, x-sw/2, mt+ph+18);
                }
            }

            g2.setColor(COR_TEXTO_SEC);
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            g2.drawString(eixoX, ml+pw/2-20, H-5);

            int lx = ml;
            for (int i = 0; i < legendas.size(); i++) {
                g2.setColor(cores.get(i));
                g2.fillRect(lx, H-mb+25, 12, 4);
                g2.setColor(COR_TEXTO_SEC);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
                g2.drawString(legendas.get(i), lx+16, H-mb+30);
                lx += g2.getFontMetrics().stringWidth(legendas.get(i))+30;
            }

            g2.setColor(COR_GRADE);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ml,mt,ml,mt+ph);
            g2.drawLine(ml,mt+ph,ml+pw,mt+ph);
            g2.dispose();
        }
    }

    // ==================== GRÁFICO DE BARRAS ====================
    static class GraficoBarras extends JPanel {
        private double[] valores;
        private String[] labels;
        private Color[]  cores;
        private String   titulo;

        GraficoBarras(String titulo, double[] vals,
                      String[] labs, Color[] cors) {
            this.titulo=titulo; this.valores=vals;
            this.labels=labs;   this.cores=cors;
            setBackground(COR_PAINEL);
            setPreferredSize(new Dimension(500,280));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int W=getWidth(), H=getHeight();
            int ml=70, mr=20, mt=40, mb=55;
            int pw=W-ml-mr, ph=H-mt-mb;
            int n = valores.length;

            g2.setColor(COR_PAINEL);
            g2.fillRect(0,0,W,H);

            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Segoe UI",Font.BOLD,14));
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(titulo,(W-fmT.stringWidth(titulo))/2,28);

            double maxV = 1;
            for (double v : valores) if (v > maxV) maxV = v;
            maxV = Math.ceil(maxV/100)*100;

            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for (int i = 0; i <= 5; i++) {
                double v = maxV*i/5.0;
                int y = mt+ph-(int)(ph*v/maxV);
                g2.setColor(COR_GRADE);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawLine(ml,y,ml+pw,y);
                g2.setColor(COR_TEXTO_SEC);
                String lbl = String.format("%.0f",v);
                g2.drawString(lbl, ml-g2.getFontMetrics().stringWidth(lbl)-5, y+4);
            }

            int bw  = (int)(pw*0.7/n);
            int gap = (int)(pw*0.3/(n+1));
            for (int i = 0; i < n; i++) {
                int x  = ml+gap*(i+1)+bw*i;
                int bh = (int)(ph*valores[i]/maxV);
                int y  = mt+ph-bh;
                Color bc = (cores!=null && i<cores.length) ? cores[i] : COR_DESTAQUE;
                GradientPaint gp = new GradientPaint(x,y,bc.brighter(),x,mt+ph,bc.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(x,y,bw,bh,6,6);
                g2.setColor(COR_TEXTO);
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                String sv = String.format("%.0f",valores[i]);
                int sw = g2.getFontMetrics().stringWidth(sv);
                if (bh > 20) g2.drawString(sv, x+(bw-sw)/2, y+15);
                g2.setColor(COR_TEXTO_SEC);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                String lb = labels[i];
                int lw = g2.getFontMetrics().stringWidth(lb);
                g2.drawString(lb, x+(bw-lw)/2, mt+ph+16);
            }

            g2.setColor(COR_GRADE);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ml,mt,ml,mt+ph);
            g2.drawLine(ml,mt+ph,ml+pw,mt+ph);
            g2.dispose();
        }
    }

    // ==================== TELA LOGIN ====================
    static class TelaLogin extends JFrame {
        TelaLogin() {
            setTitle("SCADA Solar — Login");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(460,540);
            setLocationRelativeTo(null);
            setUndecorated(true);

            JPanel root = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(COR_FUNDO);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
                    GradientPaint gp = new GradientPaint(0,0,
                        new Color(0,80,160,120),0,200,new Color(0,0,0,0));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0,0,getWidth(),200,24,24);
                    g2.dispose();
                }
            };
            root.setOpaque(false);
            root.setBorder(BorderFactory.createEmptyBorder(40,50,40,50));

            JPanel topo = new JPanel();
            topo.setOpaque(false);
            topo.setLayout(new BoxLayout(topo,BoxLayout.Y_AXIS));

            JLabel icone = new JLabel("☀",SwingConstants.CENTER);
            icone.setFont(new Font("Segoe UI Emoji",Font.PLAIN,60));
            icone.setForeground(COR_AMARELO);
            icone.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel titLbl = Fab.titulo("SCADA Solar",28,COR_DESTAQUE);
            titLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = Fab.titulo("Monitoramento Fotovoltaico",12,COR_TEXTO_SEC);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            topo.add(icone);
            topo.add(Box.createVerticalStrut(8));
            topo.add(titLbl);
            topo.add(Box.createVerticalStrut(4));
            topo.add(sub);

            JPanel form = new JPanel();
            form.setOpaque(false);
            form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
            form.setBorder(BorderFactory.createEmptyBorder(30,0,0,0));

            JLabel lUser = Fab.label("Usuário");
            lUser.setAlignmentX(Component.LEFT_ALIGNMENT);
            JTextField tfUser = Fab.campo("admin");
            tfUser.setAlignmentX(Component.LEFT_ALIGNMENT);
            tfUser.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));

            JLabel lSenha = Fab.label("Senha");
            lSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPasswordField pfSenha = Fab.senha();
            pfSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
            pfSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));

            JLabel lblErro = new JLabel(" ");
            lblErro.setForeground(COR_VERMELHO);
            lblErro.setFont(new Font("Segoe UI",Font.PLAIN,12));
            lblErro.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnLogin = Fab.botao("Entrar",COR_DESTAQUE);
            btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));

            JButton btnSair = Fab.botao("Sair",new Color(80,80,100));
            btnSair.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSair.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));

            form.add(lUser);
            form.add(Box.createVerticalStrut(6));
            form.add(tfUser);
            form.add(Box.createVerticalStrut(14));
            form.add(lSenha);
            form.add(Box.createVerticalStrut(6));
            form.add(pfSenha);
            form.add(Box.createVerticalStrut(10));
            form.add(lblErro);
            form.add(Box.createVerticalStrut(18));
            form.add(btnLogin);
            form.add(Box.createVerticalStrut(10));
            form.add(btnSair);

            JLabel rodape = new JLabel("v2.0 © 2024 SCADASolar",SwingConstants.CENTER);
            rodape.setForeground(COR_TEXTO_SEC);
            rodape.setFont(new Font("Segoe UI",Font.PLAIN,11));

            root.add(topo,BorderLayout.NORTH);
            root.add(form,BorderLayout.CENTER);
            root.add(rodape,BorderLayout.SOUTH);
            add(root);

            final int[] drag = new int[2];
            root.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    drag[0]=e.getX(); drag[1]=e.getY();
                }
            });
            root.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseDragged(MouseEvent e) {
                    setLocation(getX()+e.getX()-drag[0],
                                getY()+e.getY()-drag[1]);
                }
            });

            ActionListener acaoLogin = e -> {
                String u = tfUser.getText().trim();
                String s = new String(pfSenha.getPassword());
                String[] dados = BancoDados.get().autenticar(u,s);
                if (dados != null) {
                    dispose();
                    new TelaPrincipal(u,dados[1]).setVisible(true);
                } else {
                    lblErro.setText("✗ Usuário ou senha inválidos");
                    pfSenha.setText("");
                    tfUser.requestFocus();
                }
            };
            btnLogin.addActionListener(acaoLogin);
            pfSenha.addActionListener(acaoLogin);
            btnSair.addActionListener(e -> System.exit(0));
            setVisible(true);
        }
    }

    // ==================== TELA PRINCIPAL ====================
    static class TelaPrincipal extends JFrame {
        private String usuario, perfil;
        private JPanel painelConteudo;
        private CardLayout cardLayout;
        // CORRIGIDO: Map de botões com estado ativo como campo do painel
        private Map<String,JButton> botoesSide = new LinkedHashMap<>();
        private Map<String,Boolean> estadoAtivo = new LinkedHashMap<>();

        TelaPrincipal(String usuario, String perfil) {
            this.usuario=usuario; this.perfil=perfil;
            setTitle("SCADA Solar — "+usuario+" ["+perfil+"]");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1280,800);
            setLocationRelativeTo(null);
            construirUI();
        }

        void construirUI() {
            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(COR_FUNDO);
            root.add(criarTopbar(),BorderLayout.NORTH);
            root.add(criarSidebar(),BorderLayout.WEST);

            cardLayout = new CardLayout();
            painelConteudo = new JPanel(cardLayout);
            painelConteudo.setBackground(COR_FUNDO);

            painelConteudo.add(new PainelDashboard(usuario,perfil),"DASHBOARD");
            painelConteudo.add(new PainelUsinas(perfil),"USINAS");
            painelConteudo.add(new PainelEquipamentos(perfil),"EQUIPAMENTOS");
            painelConteudo.add(new PainelProducao(),"PRODUCAO");
            painelConteudo.add(new PainelSimulacao(),"SIMULACAO");
            painelConteudo.add(new PainelAlertas(),"ALERTAS");
            if (perfil.equals("MASTER"))
                painelConteudo.add(new PainelUsuarios(),"USUARIOS");

            JScrollPane sp = new JScrollPane(painelConteudo);
            sp.setBorder(null);
            sp.getViewport().setBackground(COR_FUNDO);
            root.add(sp,BorderLayout.CENTER);
            add(root);
            navegarPara("DASHBOARD");
        }

        JPanel criarTopbar() {
            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(new Color(10,15,25));
            bar.setPreferredSize(new Dimension(0,55));
            bar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,COR_GRADE));

            JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
            esq.setOpaque(false);
            JLabel ic = new JLabel("☀");
            ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,24));
            ic.setForeground(COR_AMARELO);
            esq.add(ic);
            esq.add(Fab.titulo("SCADA Solar",18,COR_DESTAQUE));

            JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,12));
            dir.setOpaque(false);

            JLabel relogio = new JLabel();
            relogio.setForeground(COR_TEXTO_SEC);
            relogio.setFont(new Font("Segoe UI",Font.PLAIN,13));
            // CORRIGIDO: javax.swing.Timer explícito
            javax.swing.Timer tRel = new javax.swing.Timer(1000, e ->
                relogio.setText(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss"))));
            tRel.start();
            relogio.setText(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")));

            JLabel lUser = new JLabel("👤 "+usuario+" | "+perfil);
            lUser.setForeground(COR_TEXTO_SEC);
            lUser.setFont(new Font("Segoe UI",Font.PLAIN,13));

            JButton btnSair = Fab.botao("Sair",new Color(100,30,30));
            btnSair.setPreferredSize(new Dimension(80,32));
            btnSair.addActionListener(e -> { dispose(); new TelaLogin(); });

            dir.add(relogio); dir.add(lUser); dir.add(btnSair);
            bar.add(esq,BorderLayout.WEST);
            bar.add(dir,BorderLayout.EAST);
            return bar;
        }

        JPanel criarSidebar() {
            JPanel side = new JPanel();
            side.setBackground(new Color(12,18,28));
            side.setPreferredSize(new Dimension(200,0));
            side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));
            side.setBorder(BorderFactory.createMatteBorder(0,0,0,1,COR_GRADE));

            side.add(Box.createVerticalStrut(15));
            addBotaoSide(side,"📊 Dashboard","DASHBOARD");
            addBotaoSide(side,"🏭 Usinas","USINAS");
            addBotaoSide(side,"⚙ Equipamentos","EQUIPAMENTOS");
            addBotaoSide(side,"📈 Produção","PRODUCAO");
            addBotaoSide(side,"🌤 Simulação","SIMULACAO");
            addBotaoSide(side,"🔔 Alertas","ALERTAS");
            if (perfil.equals("MASTER"))
                addBotaoSide(side,"👥 Usuários","USUARIOS");
            side.add(Box.createVerticalGlue());
            return side;
        }

        // CORRIGIDO: sem anonymous class com setAtivo — usa repaint baseado em Map
        void addBotaoSide(JPanel side, String texto, String card) {
            estadoAtivo.put(card, false);
            JButton btn = new JButton(texto) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean ativo = Boolean.TRUE.equals(estadoAtivo.get(card));
                    Color bg = ativo ? new Color(0,80,160,180) :
                               getModel().isRollover() ? new Color(30,50,80,120) :
                               new Color(0,0,0,0);
                    g2.setColor(bg);
                    g2.fillRoundRect(4,2,getWidth()-8,getHeight()-4,10,10);
                    if (ativo) {
                        g2.setColor(COR_DESTAQUE);
                        g2.fillRoundRect(0,4,4,getHeight()-8,4,4);
                    }
                    g2.setColor(ativo ? COR_DESTAQUE : COR_TEXTO_SEC);
                    g2.setFont(getFont());
                    g2.drawString(getText(),18,getHeight()/2+5);
                    g2.dispose();
                }
            };
            btn.setFont(new Font("Segoe UI",Font.PLAIN,13));
            btn.setPreferredSize(new Dimension(190,42));
            btn.setMaximumSize(new Dimension(190,42));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> navegarPara(card));
            botoesSide.put(card,btn);
            side.add(Box.createVerticalStrut(3));
            side.add(btn);
        }

        void navegarPara(String card) {
            cardLayout.show(painelConteudo,card);
            // CORRIGIDO: atualiza Map e repinta todos os botões
            botoesSide.keySet().forEach(k -> estadoAtivo.put(k, k.equals(card)));
            botoesSide.values().forEach(Component::repaint);
        }
    }

    // ==================== PAINEL DASHBOARD ====================
    static class PainelDashboard extends JPanel {
        private JLabel[] kpiVals = new JLabel[6];
        private GraficoLinha grafico;
        private JPanel painelUsinas;
        // CORRIGIDO: campo javax.swing.Timer explícito
        private javax.swing.Timer timerAtualiza;

        PainelDashboard(String usuario, String perfil) {
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
            // CORRIGIDO: javax.swing.Timer explícito
            timerAtualiza = new javax.swing.Timer(30000, e -> atualizarDados());
            timerAtualiza.start();
        }

        void construir() {
            JPanel cab = new JPanel(new BorderLayout());
            cab.setOpaque(false);
            JPanel cabTxt = new JPanel();
            cabTxt.setOpaque(false);
            cabTxt.setLayout(new BoxLayout(cabTxt,BoxLayout.Y_AXIS));
            cabTxt.add(Fab.titulo("Dashboard — Visão Geral",20,COR_TEXTO));
            cabTxt.add(Fab.titulo("Monitoramento em tempo real",12,COR_TEXTO_SEC));
            cab.add(cabTxt,BorderLayout.WEST);
            add(cab,BorderLayout.NORTH);

            // KPIs
            JPanel kpiPanel = new JPanel(new GridLayout(2,3,12,12));
            kpiPanel.setOpaque(false);
            kpiPanel.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));

            String[][] kpis = {
                {"⚡","Geração Hoje","kWh"},
                {"📅","Geração Mês","MWh"},
                {"🏆","Geração Anual","MWh"},
                {"📊","Performance Ratio","%"},
                {"🌡","Temperatura Média","°C"},
                {"🏭","Usinas Ativas","/ 4"}
            };
            for (int i = 0; i < 6; i++)
                kpiPanel.add(criarKpiCard(kpis[i][0],kpis[i][1],kpis[i][2],i));
            add(kpiPanel,BorderLayout.NORTH);

            // Centro
            JPanel centro = new JPanel(new BorderLayout(15,0));
            centro.setOpaque(false);

            // CORRIGIDO: GraficoLinha sem parâmetro eixoY
            grafico = new GraficoLinha("Produção Diária — Últimos 30 dias","Dia");
            JPanel gPanel = Fab.painel(COR_PAINEL);
            gPanel.setLayout(new BorderLayout());
            gPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            gPanel.add(grafico);

            painelUsinas = new JPanel(new GridLayout(4,1,8,8));
            painelUsinas.setOpaque(false);
            painelUsinas.setPreferredSize(new Dimension(280,300));

            centro.add(gPanel,BorderLayout.CENTER);
            centro.add(painelUsinas,BorderLayout.EAST);
            add(centro,BorderLayout.CENTER);

            atualizarDados();
        }

        JPanel criarKpiCard(String icone, String nome, String unidade, int idx) {
            JPanel card = Fab.painel(COR_PAINEL);
            card.setLayout(new BorderLayout(8,0));
            card.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

            JLabel ic = new JLabel(icone);
            ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,30));
            ic.setHorizontalAlignment(SwingConstants.CENTER);
            ic.setPreferredSize(new Dimension(42,42));

            JPanel txt = new JPanel();
            txt.setOpaque(false);
            txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));

            JLabel lNome = new JLabel(nome);
            lNome.setFont(new Font("Segoe UI",Font.PLAIN,11));
            lNome.setForeground(COR_TEXTO_SEC);

            JLabel lVal = new JLabel("--");
            lVal.setFont(new Font("Segoe UI",Font.BOLD,22));
            lVal.setForeground(COR_DESTAQUE);
            kpiVals[idx] = lVal;

            JLabel lUnit = new JLabel(unidade);
            lUnit.setFont(new Font("Segoe UI",Font.PLAIN,11));
            lUnit.setForeground(COR_TEXTO_SEC);

            txt.add(lNome); txt.add(lVal); txt.add(lUnit);
            card.add(ic,BorderLayout.WEST);
            card.add(txt,BorderLayout.CENTER);
            return card;
        }

        void atualizarDados() {
            BancoDados bd = BancoDados.get();
            LocalDate hoje = LocalDate.now();
            LocalDate mesIni  = hoje.withDayOfMonth(1);
            LocalDate anoIni  = hoje.withDayOfYear(1);

            double geracaoHoje = bd.producao.stream()
                .filter(r -> r.data.equals(hoje))
                .mapToDouble(r -> r.energiaKwh).sum();

            double geracaoMes = bd.producao.stream()
                .filter(r -> !r.data.isBefore(mesIni) && !r.data.isAfter(hoje))
                .mapToDouble(r -> r.energiaKwh).sum() / 1000.0;

            double geracaoAno = bd.producao.stream()
                .filter(r -> !r.data.isBefore(anoIni) && !r.data.isAfter(hoje))
                .mapToDouble(r -> r.energiaKwh).sum() / 1000.0;

            double prMedio = bd.producao.stream()
                .filter(r -> !r.data.isBefore(mesIni))
                .mapToDouble(r -> r.pr).average().orElse(0.0);

            double tempMed = bd.producao.stream()
                .filter(r -> r.data.equals(hoje))
                .mapToDouble(r -> r.tempMedia).average().orElse(28.5);

            long usinasAtivas = bd.usinas.stream()
                .filter(u -> u.status.equals("OPERACIONAL")).count();

            kpiVals[0].setText(String.format("%.0f", geracaoHoje));
            kpiVals[1].setText(String.format("%.1f", geracaoMes));
            kpiVals[2].setText(String.format("%.1f", geracaoAno));
            kpiVals[3].setText(String.format("%.1f", prMedio*100));
            kpiVals[4].setText(String.format("%.1f", tempMed));
            kpiVals[5].setText(String.valueOf(usinasAtivas));

            // Gráfico 30 dias
            LocalDate ini30 = hoje.minusDays(29);
            List<LocalDate> datas30 = new ArrayList<>();
            for (int i = 0; i < 30; i++) datas30.add(ini30.plusDays(i));

            grafico.limpar();
            Color[] cors = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            int ci = 0;
            for (UsinaFotovoltaica u : bd.usinas) {
                Map<LocalDate,Double> m = new LinkedHashMap<>();
                for (LocalDate d : datas30) m.put(d,0.0);
                for (RegistroProd r : bd.producao)
                    if (r.usinaId.equals(u.id) && m.containsKey(r.data))
                        m.put(r.data, r.energiaKwh/1000.0);
                double[] vals = m.values().stream()
                    .mapToDouble(Double::doubleValue).toArray();
                String nm = u.nome.substring(0,Math.min(u.nome.length(),12));
                grafico.addSerie(vals, cors[ci%cors.length], nm);
                ci++;
            }
            grafico.setLabelsX(datas30.stream()
                .map(d->d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList()));
            grafico.repaint();

            // Cards usinas
            painelUsinas.removeAll();
            Color[] corsU = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            for (int i = 0; i < bd.usinas.size(); i++) {
                UsinaFotovoltaica u = bd.usinas.get(i);
                double ger = bd.producao.stream()
                    .filter(r->r.usinaId.equals(u.id)&&r.data.equals(hoje))
                    .mapToDouble(r->r.energiaKwh).sum();
                painelUsinas.add(criarCardUsina(u,ger,corsU[i%corsU.length]));
            }
            painelUsinas.revalidate();
            painelUsinas.repaint();
        }

        JPanel criarCardUsina(UsinaFotovoltaica u, double ger, Color cor) {
            JPanel card = Fab.painel(COR_PAINEL2);
            card.setLayout(new BorderLayout(8,0));
            card.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

            JPanel esq = new JPanel();
            esq.setOpaque(false);
            esq.setLayout(new BoxLayout(esq,BoxLayout.Y_AXIS));

            JLabel nm = new JLabel(u.nome.length()>20?u.nome.substring(0,20)+"…":u.nome);
            nm.setFont(new Font("Segoe UI",Font.BOLD,12));
            nm.setForeground(COR_TEXTO);

            JLabel loc = new JLabel("📍 "+u.localizacao);
            loc.setFont(new Font("Segoe UI",Font.PLAIN,11));
            loc.setForeground(COR_TEXTO_SEC);

            JLabel cap = new JLabel(String.format("%.0f kWp | Hoje: %.0f kWh",
                u.capacidadeKwp, ger));
            cap.setFont(new Font("Segoe UI",Font.PLAIN,11));
            cap.setForeground(cor);

            esq.add(nm); esq.add(loc); esq.add(cap);

            JLabel stL = new JLabel("●");
            stL.setForeground(u.status.equals("OPERACIONAL")?COR_VERDE:COR_AMARELO);
            stL.setFont(new Font("Segoe UI",Font.BOLD,18));
            stL.setToolTipText(u.status);

            card.add(esq,BorderLayout.CENTER);
            card.add(stL,BorderLayout.EAST);
            return card;
        }
    }

    // ==================== PAINEL USINAS ====================
    static class PainelUsinas extends JPanel {
        private String perfil;
        private DefaultTableModel modelo;
        private JTable tabela;

        PainelUsinas(String perfil) {
            this.perfil=perfil;
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
        }

        void construir() {
            JPanel cab = new JPanel(new BorderLayout());
            cab.setOpaque(false);
            cab.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
            cab.add(Fab.titulo("Gestão de Usinas",18,COR_TEXTO),BorderLayout.WEST);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
            btns.setOpaque(false);
            if (!perfil.equals("VISUALIZADOR")) {
                JButton btnAdd = Fab.botao("+ Nova Usina",COR_VERDE);
                btnAdd.setPreferredSize(new Dimension(130,36));
                btnAdd.addActionListener(e -> dialogoUsina(null));
                btns.add(btnAdd);

                JButton btnEdit = Fab.botao("✏ Editar",COR_DESTAQUE);
                btnEdit.setPreferredSize(new Dimension(100,36));
                btnEdit.addActionListener(e -> editarUsina());
                btns.add(btnEdit);

                if (perfil.equals("MASTER")) {
                    JButton btnDel = Fab.botao("🗑 Excluir",COR_VERMELHO);
                    btnDel.setPreferredSize(new Dimension(100,36));
                    btnDel.addActionListener(e -> excluirUsina());
                    btns.add(btnDel);
                }
            }
            JButton btnAt = Fab.botao("↺ Atualizar",new Color(60,80,120));
            btnAt.setPreferredSize(new Dimension(110,36));
            btnAt.addActionListener(e -> carregarTabela());
            btns.add(btnAt);
            cab.add(btns,BorderLayout.EAST);
            add(cab,BorderLayout.NORTH);

            String[] cols = {"ID","Nome","Localização","Cap.(kWp)",
                             "Área(m²)","Ano","Responsável","Status"};
            modelo = new DefaultTableModel(cols,0) {
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            tabela = new JTable(modelo);
            estilizarTabela(tabela);
            carregarTabela();

            JScrollPane sp = new JScrollPane(tabela);
            sp.getViewport().setBackground(COR_PAINEL);
            sp.setBorder(new RoundBorder(COR_GRADE,10,1));
            add(sp,BorderLayout.CENTER);
            add(criarResumo(),BorderLayout.SOUTH);
        }

        JPanel criarResumo() {
            JPanel p = new JPanel(new GridLayout(1,4,10,0));
            p.setOpaque(false);
            p.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));
            BancoDados bd = BancoDados.get();
            double totalKwp = bd.usinas.stream().mapToDouble(u->u.capacidadeKwp).sum();
            long opCount = bd.usinas.stream().filter(u->u.status.equals("OPERACIONAL")).count();
            double totalGen = bd.producao.stream()
                .filter(r->r.data.equals(LocalDate.now()))
                .mapToDouble(r->r.energiaKwh).sum();
            String[][] info = {
                {"🏭","Total Usinas",String.valueOf(bd.usinas.size())},
                {"⚡","Cap. Total",String.format("%.0f kWp",totalKwp)},
                {"✅","Operacionais",opCount+" / "+bd.usinas.size()},
                {"📊","Geração Hoje",String.format("%.0f kWh",totalGen)}
            };
            Color[] cors = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            for (int i=0;i<4;i++) {
                JPanel c = Fab.painel(COR_PAINEL);
                c.setLayout(new BorderLayout(8,0));
                c.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
                JLabel ic = new JLabel(info[i][0]);
                ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,26));
                JPanel txt = new JPanel();
                txt.setOpaque(false);
                txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
                JLabel l1 = new JLabel(info[i][1]);
                l1.setFont(new Font("Segoe UI",Font.PLAIN,11));
                l1.setForeground(COR_TEXTO_SEC);
                JLabel l2 = new JLabel(info[i][2]);
                l2.setFont(new Font("Segoe UI",Font.BOLD,16));
                l2.setForeground(cors[i]);
                txt.add(l1); txt.add(l2);
                c.add(ic,BorderLayout.WEST); c.add(txt,BorderLayout.CENTER);
                p.add(c);
            }
            return p;
        }

        void carregarTabela() {
            modelo.setRowCount(0);
            for (UsinaFotovoltaica u : BancoDados.get().usinas)
                modelo.addRow(new Object[]{u.id,u.nome,u.localizacao,
                    String.format("%.1f",u.capacidadeKwp),
                    String.format("%.0f",u.areaM2),
                    u.anoInstalacao,u.responsavel,u.status});
        }

        void dialogoUsina(UsinaFotovoltaica usina) {
            JDialog d = new JDialog(
                (JFrame)SwingUtilities.getWindowAncestor(this),
                usina==null?"Nova Usina":"Editar Usina",true);
            d.setSize(440,460);
            d.setLocationRelativeTo(this);
            d.getContentPane().setBackground(COR_FUNDO);
            d.setLayout(new BorderLayout(10,10));

            JPanel form = new JPanel(new GridLayout(8,2,8,10));
            form.setBackground(COR_PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            String nextId = "U"+String.format("%03d",BancoDados.get().usinas.size()+1);
            JTextField tfId   = Fab.campo(usina==null?nextId:usina.id);
            JTextField tfNome = Fab.campo(usina==null?"":usina.nome);
            JTextField tfLoc  = Fab.campo(usina==null?"":usina.localizacao);
            JTextField tfCap  = Fab.campo(usina==null?"":String.valueOf(usina.capacidadeKwp));
            JTextField tfArea = Fab.campo(usina==null?"":String.valueOf(usina.areaM2));
            JTextField tfAno  = Fab.campo(usina==null?"2024":String.valueOf(usina.anoInstalacao));
            JTextField tfResp = Fab.campo(usina==null?"":usina.responsavel);
            JComboBox<String> cbSt = Fab.combo(
                new String[]{"OPERACIONAL","MANUTENCAO","INATIVA"});
            if (usina!=null) cbSt.setSelectedItem(usina.status);

            form.add(Fab.label("ID:"));     form.add(tfId);
            form.add(Fab.label("Nome:"));   form.add(tfNome);
            form.add(Fab.label("Local.:")); form.add(tfLoc);
            form.add(Fab.label("kWp:"));    form.add(tfCap);
            form.add(Fab.label("Área m²:")); form.add(tfArea);
            form.add(Fab.label("Ano:"));    form.add(tfAno);
            form.add(Fab.label("Resp.:"));  form.add(tfResp);
            form.add(Fab.label("Status:")); form.add(cbSt);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.setBackground(COR_FUNDO);
            JButton btnOk  = Fab.botao("Salvar",COR_VERDE);
            JButton btnCan = Fab.botao("Cancelar",new Color(80,80,100));
            btnCan.addActionListener(e->d.dispose());
            btnOk.addActionListener(e -> {
                try {
                    BancoDados bd = BancoDados.get();
                    if (usina==null) {
                        bd.usinas.add(new UsinaFotovoltaica(
                            tfId.getText().trim(),tfNome.getText().trim(),
                            tfLoc.getText().trim(),
                            Double.parseDouble(tfCap.getText().trim()),
                            Double.parseDouble(tfArea.getText().trim()),
                            Integer.parseInt(tfAno.getText().trim()),
                            tfResp.getText().trim(),
                            (String)cbSt.getSelectedItem()));
                    } else {
                        usina.nome=tfNome.getText().trim();
                        usina.localizacao=tfLoc.getText().trim();
                        usina.capacidadeKwp=Double.parseDouble(tfCap.getText().trim());
                        usina.areaM2=Double.parseDouble(tfArea.getText().trim());
                        usina.anoInstalacao=Integer.parseInt(tfAno.getText().trim());
                        usina.responsavel=tfResp.getText().trim();
                        usina.status=(String)cbSt.getSelectedItem();
                    }
                    bd.salvarUsinas();
                    carregarTabela();
                    d.dispose();
                } catch(NumberFormatException ex) {
                    JOptionPane.showMessageDialog(d,"Valores inválidos!",
                        "Erro",JOptionPane.ERROR_MESSAGE);
                }
            });
            btns.add(btnCan); btns.add(btnOk);
            d.add(form,BorderLayout.CENTER);
            d.add(btns,BorderLayout.SOUTH);
            d.setVisible(true);
        }

        void editarUsina() {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,"Selecione uma usina.",
                    "Aviso",JOptionPane.WARNING_MESSAGE); return;
            }
            String id = (String)modelo.getValueAt(row,0);
            BancoDados.get().usinas.stream()
                .filter(u->u.id.equals(id)).findFirst()
                .ifPresent(this::dialogoUsina);
        }

        void excluirUsina() {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,"Selecione uma usina.",
                    "Aviso",JOptionPane.WARNING_MESSAGE); return;
            }
            String id = (String)modelo.getValueAt(row,0);
            if (JOptionPane.showConfirmDialog(this,"Excluir usina "+id+"?",
                    "Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                BancoDados.get().usinas.removeIf(u->u.id.equals(id));
                BancoDados.get().salvarUsinas();
                carregarTabela();
            }
        }
    }

    // ==================== PAINEL EQUIPAMENTOS ====================
    static class PainelEquipamentos extends JPanel {
        private String perfil;
        private DefaultTableModel modelo;
        private JTable tabela;
        private JComboBox<String> cbTipo, cbUsina;

        PainelEquipamentos(String perfil) {
            this.perfil=perfil;
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
        }

        void construir() {
            JPanel cab = new JPanel(new BorderLayout(10,5));
            cab.setOpaque(false);
            cab.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
            cab.add(Fab.titulo("Gestão de Equipamentos",18,COR_TEXTO),
                    BorderLayout.NORTH);

            JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
            filtros.setOpaque(false);
            filtros.add(Fab.label("Tipo:"));
            cbTipo = Fab.combo(new String[]{"TODOS","INVERSOR","STRING","MODULO","MEDIDOR"});
            cbTipo.addActionListener(e->carregarTabela());
            filtros.add(cbTipo);
            filtros.add(Fab.label("Usina:"));
            List<String> opts = new ArrayList<>();
            opts.add("TODAS");
            BancoDados.get().usinas.forEach(u->opts.add(u.id+" - "+u.nome));
            cbUsina = Fab.combo(opts.toArray(new String[0]));
            cbUsina.addActionListener(e->carregarTabela());
            filtros.add(cbUsina);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
            btns.setOpaque(false);
            if (!perfil.equals("VISUALIZADOR")) {
                JButton btnAdd = Fab.botao("+ Equipamento",COR_VERDE);
                btnAdd.setPreferredSize(new Dimension(140,36));
                btnAdd.addActionListener(e->dialogoEquipamento());
                btns.add(btnAdd);
                if (perfil.equals("MASTER")) {
                    JButton btnDel = Fab.botao("🗑 Excluir",COR_VERMELHO);
                    btnDel.setPreferredSize(new Dimension(100,36));
                    btnDel.addActionListener(e->excluirEquipamento());
                    btns.add(btnDel);
                }
            }

            JPanel fb = new JPanel(new BorderLayout());
            fb.setOpaque(false);
            fb.add(filtros,BorderLayout.WEST);
            fb.add(btns,BorderLayout.EAST);
            cab.add(fb,BorderLayout.SOUTH);
            add(cab,BorderLayout.NORTH);

            String[] cols = {"ID","Tipo","Nome","Usina","Param.1",
                             "Param.2","Detalhes","Status","Instalação"};
            modelo = new DefaultTableModel(cols,0) {
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            tabela = new JTable(modelo);
            estilizarTabela(tabela);
            carregarTabela();

            JScrollPane sp = new JScrollPane(tabela);
            sp.getViewport().setBackground(COR_PAINEL);
            sp.setBorder(new RoundBorder(COR_GRADE,10,1));
            add(sp,BorderLayout.CENTER);
            add(criarContadores(),BorderLayout.SOUTH);
        }

        JPanel criarContadores() {
            JPanel p = new JPanel(new GridLayout(1,4,10,0));
            p.setOpaque(false);
            p.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));
            BancoDados bd = BancoDados.get();
            long nI = bd.equipamentos.stream().filter(e->e.getTipo().equals("INVERSOR")).count();
            long nS = bd.equipamentos.stream().filter(e->e.getTipo().equals("STRING")).count();
            long nM = bd.equipamentos.stream().filter(e->e.getTipo().equals("MODULO")).count();
            long nD = bd.equipamentos.stream().filter(e->e.getTipo().equals("MEDIDOR")).count();
            String[][] info = {
                {"⚡","Inversores",String.valueOf(nI)},
                {"🔗","String Boxes",String.valueOf(nS)},
                {"☀","Módulos",String.valueOf(nM)},
                {"📡","Medidores",String.valueOf(nD)}
            };
            Color[] cors = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            for (int i=0;i<4;i++) {
                JPanel c = Fab.painel(COR_PAINEL);
                c.setLayout(new BorderLayout(8,0));
                c.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
                JLabel ic = new JLabel(info[i][0]);
                ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,26));
                JPanel txt = new JPanel();
                txt.setOpaque(false);
                txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
                JLabel l1 = new JLabel(info[i][1]);
                l1.setFont(new Font("Segoe UI",Font.PLAIN,11));
                l1.setForeground(COR_TEXTO_SEC);
                JLabel l2 = new JLabel(info[i][2]);
                l2.setFont(new Font("Segoe UI",Font.BOLD,20));
                l2.setForeground(cors[i]);
                txt.add(l1); txt.add(l2);
                c.add(ic,BorderLayout.WEST); c.add(txt,BorderLayout.CENTER);
                p.add(c);
            }
            return p;
        }

        void carregarTabela() {
            modelo.setRowCount(0);
            String tf = (String)cbTipo.getSelectedItem();
            String uf = (String)cbUsina.getSelectedItem();
            String uid = (uf!=null&&!uf.equals("TODAS")) ? uf.split(" - ")[0] : null;

            for (Equipamento eq : BancoDados.get().equipamentos) {
                if (!tf.equals("TODOS") && !eq.getTipo().equals(tf)) continue;
                if (uid!=null && !eq.usinaId.equals(uid)) continue;
                String p1="",p2="",det="";
                if (eq instanceof Inversor) {
                    Inversor v=(Inversor)eq;
                    p1=v.potenciaNominal+" kW"; p2=v.eficiencia+" %";
                    det=v.fabricante+"/"+v.modelo;
                } else if (eq instanceof StringSolar) {
                    StringSolar v=(StringSolar)eq;
                    p1=v.numModulos+" mód"; p2=v.tensaoNominal+" V";
                    det=v.correnteNominal+" A";
                } else if (eq instanceof ModuloFotovoltaico) {
                    ModuloFotovoltaico v=(ModuloFotovoltaico)eq;
                    p1=v.potenciaPico+" Wp"; p2=v.eficiencia+" %";
                    det=v.fabricante+"/"+v.tecnologia;
                } else if (eq instanceof MedidorBidirecional) {
                    MedidorBidirecional v=(MedidorBidirecional)eq;
                    p1=v.capacidadeKw+" kW"; p2=v.protocolo;
                    det=v.numeroSerie;
                }
                modelo.addRow(new Object[]{eq.id,eq.getTipo(),eq.nome,
                    eq.usinaId,p1,p2,det,eq.status,eq.dataInstalacao});
            }
        }

        void dialogoEquipamento() {
            JDialog d = new JDialog(
                (JFrame)SwingUtilities.getWindowAncestor(this),
                "Novo Equipamento",true);
            d.setSize(420,380);
            d.setLocationRelativeTo(this);
            d.getContentPane().setBackground(COR_FUNDO);
            d.setLayout(new BorderLayout(10,10));

            JPanel form = new JPanel(new GridLayout(7,2,8,10));
            form.setBackground(COR_PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            JComboBox<String> cTipo = Fab.combo(
                new String[]{"INVERSOR","STRING","MODULO","MEDIDOR"});
            List<String> uOpts = BancoDados.get().usinas.stream()
                .map(u->u.id+" - "+u.nome).collect(Collectors.toList());
            JComboBox<String> cUsina = Fab.combo(uOpts.toArray(new String[0]));
            JTextField tfId   = Fab.campo("EQ"+String.format("%03d",
                BancoDados.get().equipamentos.size()+1));
            JTextField tfNome = Fab.campo("");
            JTextField tfP1   = Fab.campo("Potência/Módulos/Cap.");
            JTextField tfP2   = Fab.campo("Eficiência/Tensão/Prot.");
            JTextField tfP3   = Fab.campo("Fabricante/Tecn./Série");

            form.add(Fab.label("Tipo:"));   form.add(cTipo);
            form.add(Fab.label("Usina:"));  form.add(cUsina);
            form.add(Fab.label("ID:"));     form.add(tfId);
            form.add(Fab.label("Nome:"));   form.add(tfNome);
            form.add(Fab.label("Param.1:")); form.add(tfP1);
            form.add(Fab.label("Param.2:")); form.add(tfP2);
            form.add(Fab.label("Param.3:")); form.add(tfP3);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.setBackground(COR_FUNDO);
            JButton btnOk  = Fab.botao("Salvar",COR_VERDE);
            JButton btnCan = Fab.botao("Cancelar",new Color(80,80,100));
            btnCan.addActionListener(ev->d.dispose());
            btnOk.addActionListener(ev -> {
                try {
                    String tipo = (String)cTipo.getSelectedItem();
                    String uid2 = ((String)cUsina.getSelectedItem()).split(" - ")[0];
                    String id2  = tfId.getText().trim();
                    String nome = tfNome.getText().trim();
                    String data = LocalDate.now().toString();
                    BancoDados bd = BancoDados.get();
                    switch (tipo) {
                        case "INVERSOR":
                            bd.equipamentos.add(new Inversor(id2,nome,uid2,
                                Double.parseDouble(tfP1.getText().trim()),
                                Double.parseDouble(tfP2.getText().trim()),
                                tfP3.getText().trim(),tfP3.getText().trim(),
                                "OPERACIONAL",data)); break;
                        case "STRING":
                            bd.equipamentos.add(new StringSolar(id2,nome,uid2,
                                Integer.parseInt(tfP1.getText().trim()),
                                Double.parseDouble(tfP2.getText().trim()),
                                Double.parseDouble(tfP3.getText().trim()),
                                "OPERACIONAL",data)); break;
                        case "MODULO":
                            bd.equipamentos.add(new ModuloFotovoltaico(id2,nome,uid2,
                                Double.parseDouble(tfP1.getText().trim()),
                                Double.parseDouble(tfP2.getText().trim()),
                                tfP3.getText().trim(),tfP3.getText().trim(),
                                "OPERACIONAL",data)); break;
                        default:
                            bd.equipamentos.add(new MedidorBidirecional(id2,nome,uid2,
                                Double.parseDouble(tfP1.getText().trim()),
                                tfP2.getText().trim(),tfP3.getText().trim(),
                                "OPERACIONAL",data)); break;
                    }
                    bd.salvarEquipamentos();
                    carregarTabela();
                    d.dispose();
                } catch(NumberFormatException ex) {
                    JOptionPane.showMessageDialog(d,"Parâmetros inválidos!",
                        "Erro",JOptionPane.ERROR_MESSAGE);
                }
            });
            btns.add(btnCan); btns.add(btnOk);
            d.add(form,BorderLayout.CENTER);
            d.add(btns,BorderLayout.SOUTH);
            d.setVisible(true);
        }

        void excluirEquipamento() {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,"Selecione um equipamento.");
                return;
            }
            String id = (String)modelo.getValueAt(row,0);
            if (JOptionPane.showConfirmDialog(this,"Excluir "+id+"?",
                    "Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                BancoDados.get().equipamentos.removeIf(e->e.id.equals(id));
                BancoDados.get().salvarEquipamentos();
                carregarTabela();
            }
        }
    }

    // ==================== PAINEL PRODUÇÃO ====================
    static class PainelProducao extends JPanel {
        private JComboBox<String> cbUsina, cbPeriodo;
        private GraficoLinha graficoLinha;
        private GraficoBarras graficoBarras;
        private JPanel gBarPanel;
        private DefaultTableModel modeloTab;
        private JLabel[] resumoLbls = new JLabel[4];

        PainelProducao() {
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
        }

        void construir() {
            JPanel ctrl = new JPanel(new BorderLayout());
            ctrl.setOpaque(false);
            ctrl.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
            ctrl.add(Fab.titulo("Análise de Produção",18,COR_TEXTO),
                     BorderLayout.WEST);

            JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
            filtros.setOpaque(false);
            filtros.add(Fab.label("Usina:"));
            List<String> opts = new ArrayList<>();
            opts.add("TODAS");
            BancoDados.get().usinas.forEach(u->opts.add(u.id+" - "+u.nome));
            cbUsina = Fab.combo(opts.toArray(new String[0]));
            cbPeriodo = Fab.combo(new String[]{
                "Últimos 7 dias","Últimos 30 dias","Últimos 90 dias",
                "Mês Atual","Ano Atual"});
            JButton btnF = Fab.botao("Filtrar",COR_DESTAQUE);
            btnF.setPreferredSize(new Dimension(100,36));
            btnF.addActionListener(e->atualizar());
            JButton btnE = Fab.botao("📥 Exportar",new Color(40,100,60));
            btnE.setPreferredSize(new Dimension(110,36));
            btnE.addActionListener(e->exportar());
            filtros.add(cbUsina); filtros.add(cbPeriodo);
            filtros.add(btnF); filtros.add(btnE);
            ctrl.add(filtros,BorderLayout.EAST);
            add(ctrl,BorderLayout.NORTH);

            JPanel centro = new JPanel(new GridLayout(1,2,15,0));
            centro.setOpaque(false);

            // CORRIGIDO: GraficoLinha sem parâmetro eixoY
            graficoLinha = new GraficoLinha("Produção Diária","Dia");
            JPanel gLP = Fab.painel(COR_PAINEL);
            gLP.setLayout(new BorderLayout());
            gLP.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            gLP.add(graficoLinha);

            gBarPanel = Fab.painel(COR_PAINEL);
            gBarPanel.setLayout(new BorderLayout());
            gBarPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            graficoBarras = new GraficoBarras("Comparativo",
                new double[]{0},new String[]{""},null);
            gBarPanel.add(graficoBarras);

            centro.add(gLP); centro.add(gBarPanel);
            add(centro,BorderLayout.CENTER);

            JPanel sul = new JPanel(new BorderLayout(10,10));
            sul.setOpaque(false);

            JPanel res = new JPanel(new GridLayout(1,4,10,0));
            res.setOpaque(false);
            res.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
            String[] rn = {"Total (kWh)","Média/dia","PR Médio (%)","Melhor dia"};
            Color[] rc = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            for (int i=0;i<4;i++) {
                JPanel c = Fab.painel(COR_PAINEL);
                c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
                c.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
                JLabel l1=new JLabel(rn[i]);
                l1.setFont(new Font("Segoe UI",Font.PLAIN,11));
                l1.setForeground(COR_TEXTO_SEC);
                JLabel l2=new JLabel("--");
                l2.setFont(new Font("Segoe UI",Font.BOLD,16));
                l2.setForeground(rc[i]);
                resumoLbls[i]=l2;
                c.add(l1); c.add(l2);
                res.add(c);
            }

            String[] cols = {"Usina","Data","Energia(kWh)","Irrad.","Temp.°C","PR"};
            modeloTab = new DefaultTableModel(cols,0) {
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            JTable tbl = new JTable(modeloTab);
            estilizarTabela(tbl);
            JScrollPane sp = new JScrollPane(tbl);
            sp.setPreferredSize(new Dimension(0,190));
            sp.getViewport().setBackground(COR_PAINEL);
            sp.setBorder(new RoundBorder(COR_GRADE,10,1));

            sul.add(res,BorderLayout.NORTH);
            sul.add(sp,BorderLayout.CENTER);
            add(sul,BorderLayout.SOUTH);
            atualizar();
        }

        void atualizar() {
            BancoDados bd = BancoDados.get();
            LocalDate hoje = LocalDate.now();
            String per = (String)cbPeriodo.getSelectedItem();
            LocalDate inicio;
            switch (per==null?"":per) {
                case "Últimos 7 dias":  inicio=hoje.minusDays(6); break;
                case "Últimos 30 dias": inicio=hoje.minusDays(29); break;
                case "Mês Atual":       inicio=hoje.withDayOfMonth(1); break;
                case "Ano Atual":       inicio=hoje.withDayOfYear(1); break;
                default:                inicio=hoje.minusDays(89); break;
            }
            final LocalDate ini = inicio;

            String uf = (String)cbUsina.getSelectedItem();
            String uid = (uf!=null&&!uf.equals("TODAS")) ? uf.split(" - ")[0] : null;

            List<RegistroProd> fil = bd.producao.stream()
                .filter(r->!r.data.isBefore(ini)&&!r.data.isAfter(hoje))
                .filter(r->uid==null||r.usinaId.equals(uid))
                .sorted(Comparator.comparing(r->r.data))
                .collect(Collectors.toList());

            List<LocalDate> datas = fil.stream().map(r->r.data)
                .distinct().sorted().collect(Collectors.toList());

            graficoLinha.limpar();
            Color[] cors = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            int ci=0;
            Map<String,List<RegistroProd>> porU = fil.stream()
                .collect(Collectors.groupingBy(r->r.usinaId));
            for (Map.Entry<String,List<RegistroProd>> ent : porU.entrySet()) {
                Map<LocalDate,Double> pd = ent.getValue().stream()
                    .collect(Collectors.groupingBy(r->r.data,
                             Collectors.summingDouble(r->r.energiaKwh)));
                double[] vals = datas.stream()
                    .mapToDouble(d->pd.getOrDefault(d,0.0)).toArray();
                String nm = bd.usinas.stream()
                    .filter(u->u.id.equals(ent.getKey()))
                    .map(u->u.nome.substring(0,Math.min(u.nome.length(),12)))
                    .findFirst().orElse(ent.getKey());
                graficoLinha.addSerie(vals,cors[ci%cors.length],nm);
                ci++;
            }
            graficoLinha.setLabelsX(datas.stream()
                .map(d->d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList()));
            graficoLinha.repaint();

            // Barras
            double[] bVals = bd.usinas.stream()
                .mapToDouble(u->fil.stream()
                    .filter(r->r.usinaId.equals(u.id))
                    .mapToDouble(r->r.energiaKwh).sum())
                .toArray();
            String[] bLabs = bd.usinas.stream()
                .map(u->u.nome.substring(0,Math.min(u.nome.length(),8)))
                .toArray(String[]::new);
            Color[] bCors = {COR_DESTAQUE,COR_VERDE,COR_AMARELO,COR_LARANJA};
            gBarPanel.remove(graficoBarras);
            graficoBarras = new GraficoBarras("Comparativo por Usina",
                bVals,bLabs,bCors);
            gBarPanel.add(graficoBarras);
            gBarPanel.revalidate(); gBarPanel.repaint();

            // Resumo
            double total = fil.stream().mapToDouble(r->r.energiaKwh).sum();
            double media = datas.isEmpty()?0:total/datas.size();
            double prM   = fil.stream().mapToDouble(r->r.pr).average().orElse(0);
            double melhor= fil.stream()
                .collect(Collectors.groupingBy(r->r.data,
                         Collectors.summingDouble(r->r.energiaKwh)))
                .values().stream().mapToDouble(Double::doubleValue).max().orElse(0);

            resumoLbls[0].setText(String.format("%.0f",total));
            resumoLbls[1].setText(String.format("%.0f",media));
            resumoLbls[2].setText(String.format("%.1f",prM*100));
            resumoLbls[3].setText(String.format("%.0f kWh",melhor));

            modeloTab.setRowCount(0);
            fil.stream().sorted((a,b)->b.data.compareTo(a.data))
                .limit(100).forEach(r -> {
                    String nm = bd.usinas.stream()
                        .filter(u->u.id.equals(r.usinaId))
                        .map(u->u.nome).findFirst().orElse(r.usinaId);
                    modeloTab.addRow(new Object[]{nm,
                        r.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        String.format("%.1f",r.energiaKwh),
                        String.format("%.2f",r.irradiancia),
                        String.format("%.1f",r.tempMedia),
                        String.format("%.3f",r.pr)});
                });
        }

        void exportar() {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("producao_"+LocalDate.now()+".csv"));
            if (fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
                try (PrintWriter pw = new PrintWriter(
                        new FileWriter(fc.getSelectedFile()))) {
                    pw.println("Usina,Data,Energia_kWh,Irradiancia,Temp_C,PR");
                    for (int i=0;i<modeloTab.getRowCount();i++) {
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<modeloTab.getColumnCount();j++) {
                            if (j>0) sb.append(",");
                            sb.append(modeloTab.getValueAt(i,j));
                        }
                        pw.println(sb);
                    }
                    JOptionPane.showMessageDialog(this,"Exportado!","OK",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch(IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erro: "+ex.getMessage(),"Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // ==================== PAINEL SIMULAÇÃO ====================
    static class PainelSimulacao extends JPanel {
        private JSlider slIrrad, slTemp, slNuvens, slSombra;
        private JLabel lblEnergia, lblPR, lblPerdas, lblPot;
        private JComboBox<String> cbUsina;
        private GraficoBarras grafSim;
        private JPanel grafPanel;

        PainelSimulacao() {
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(15,15));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
        }

        void construir() {
            add(Fab.titulo("Simulação Ambiental — Impacto na Geração",18,COR_TEXTO),
                BorderLayout.NORTH);

            JPanel esq = new JPanel();
            esq.setOpaque(false);
            esq.setLayout(new BoxLayout(esq,BoxLayout.Y_AXIS));
            esq.setPreferredSize(new Dimension(370,0));

            List<String> uOpts = BancoDados.get().usinas.stream()
                .map(u->u.id+" - "+u.nome).collect(Collectors.toList());
            cbUsina = Fab.combo(uOpts.toArray(new String[0]));
            cbUsina.addActionListener(e->simular());

            JPanel pUs = new JPanel();
            pUs.setOpaque(false);
            pUs.setLayout(new BoxLayout(pUs,BoxLayout.Y_AXIS));
            JLabel lUs = Fab.titulo("Usina Selecionada",12,COR_TEXTO_SEC);
            lUs.setAlignmentX(Component.LEFT_ALIGNMENT);
            cbUsina.setAlignmentX(Component.LEFT_ALIGNMENT);
            pUs.add(lUs); pUs.add(Box.createVerticalStrut(4)); pUs.add(cbUsina);
            esq.add(pUs); esq.add(Box.createVerticalStrut(10));

            slIrrad  = mkSlider(0,1000,650);
            slTemp   = mkSlider(-10,60,28);
            slNuvens = mkSlider(0,100,20);
            slSombra = mkSlider(0,50,5);

            esq.add(mkSliderPanel("☀ Irradiância (W/m²)",slIrrad,"W/m²"));
            esq.add(Box.createVerticalStrut(8));
            esq.add(mkSliderPanel("🌡 Temperatura (°C)",slTemp,"°C"));
            esq.add(Box.createVerticalStrut(8));
            esq.add(mkSliderPanel("☁ Cobertura Nuvens (%)",slNuvens,"%"));
            esq.add(Box.createVerticalStrut(8));
            esq.add(mkSliderPanel("🏗 Sombreamento (%)",slSombra,"%"));
            esq.add(Box.createVerticalStrut(12));

            JButton btnSim = Fab.botao("▶ Simular",COR_VERDE);
            btnSim.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSim.setMaximumSize(new Dimension(220,44));
            btnSim.addActionListener(e->simular());
            esq.add(btnSim);
            esq.add(Box.createVerticalStrut(12));

            JPanel res = Fab.painel(COR_PAINEL);
            res.setLayout(new GridLayout(4,2,8,8));
            res.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
            res.setAlignmentX(Component.CENTER_ALIGNMENT);
            res.setMaximumSize(new Dimension(370,170));

            lblEnergia = mkLblRes(); lblPR = mkLblRes();
            lblPerdas  = mkLblRes(); lblPot = mkLblRes();

            res.add(Fab.label("Energia (kWh/dia):")); res.add(lblEnergia);
            res.add(Fab.label("PR estimado:"));       res.add(lblPR);
            res.add(Fab.label("Perdas térmicas:"));   res.add(lblPerdas);
            res.add(Fab.label("Potência (kW):"));     res.add(lblPot);
            esq.add(res);

            grafPanel = Fab.painel(COR_PAINEL);
            grafPanel.setLayout(new BorderLayout());
            grafPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            grafSim = new GraficoBarras("Balanço Energético",
                new double[]{0,0,0,0},
                new String[]{"Bruta","T.Térm.","T.Nuv.","Líquida"},
                new Color[]{COR_VERDE,COR_VERMELHO,COR_AMARELO,COR_DESTAQUE});
            grafPanel.add(grafSim);

            add(esq,BorderLayout.WEST);
            add(grafPanel,BorderLayout.CENTER);
            simular();
        }

        JSlider mkSlider(int min, int max, int val) {
            JSlider s = new JSlider(min,max,val);
            s.setBackground(COR_FUNDO);
            s.setForeground(COR_TEXTO);
            s.addChangeListener(e->simular());
            return s;
        }

        JPanel mkSliderPanel(String nome, JSlider slider, String unidade) {
            JPanel p = Fab.painel(COR_PAINEL);
            p.setLayout(new BorderLayout(8,4));
            p.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
            p.setMaximumSize(new Dimension(370,68));
            p.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lNome = new JLabel(nome);
            lNome.setForeground(COR_TEXTO);
            lNome.setFont(new Font("Segoe UI",Font.PLAIN,12));

            JLabel lVal = new JLabel(slider.getValue()+" "+unidade);
            lVal.setForeground(COR_DESTAQUE);
            lVal.setFont(new Font("Segoe UI",Font.BOLD,12));
            lVal.setPreferredSize(new Dimension(75,20));
            lVal.setHorizontalAlignment(SwingConstants.RIGHT);
            slider.addChangeListener(e->lVal.setText(slider.getValue()+" "+unidade));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(lNome,BorderLayout.WEST);
            top.add(lVal,BorderLayout.EAST);
            p.add(top,BorderLayout.NORTH);
            p.add(slider,BorderLayout.CENTER);
            return p;
        }

        JLabel mkLblRes() {
            JLabel l = new JLabel("--");
            l.setForeground(COR_DESTAQUE);
            l.setFont(new Font("Segoe UI",Font.BOLD,14));
            return l;
        }

        void simular() {
            String sel = (String)cbUsina.getSelectedItem();
            if (sel==null) return;
            String uid = sel.split(" - ")[0];
            UsinaFotovoltaica u = BancoDados.get().usinas.stream()
                .filter(x->x.id.equals(uid)).findFirst().orElse(null);
            if (u==null) return;

            double irrad  = slIrrad.getValue();
            double temp   = slTemp.getValue();
            double nuvens = slNuvens.getValue()/100.0;
            double sombra = slSombra.getValue()/100.0;

            double irradEf   = irrad*(1-nuvens*0.8)*(1-sombra);
            double perdaTerm = Math.max(0,(temp-25)*0.004);
            double pr        = 0.78*(1-perdaTerm);
            double bruta     = u.capacidadeKwp*(irradEf/1000.0)*5.5;
            double pTerm     = bruta*perdaTerm;
            double pNuv      = bruta*nuvens*0.8;
            double liquida   = Math.max(0, bruta-pTerm-pNuv);
            double pot       = liquida/5.5;

            lblEnergia.setText(String.format("%.1f kWh",liquida));
            lblPR.setText(String.format("%.1f %%",pr*100));
            lblPerdas.setText(String.format("%.1f %%",perdaTerm*100));
            lblPot.setText(String.format("%.1f kW",pot));

            grafPanel.remove(grafSim);
            grafSim = new GraficoBarras("Balanço Energético (kWh/dia)",
                new double[]{bruta,pTerm,pNuv,liquida},
                new String[]{"Bruta","T.Térm.","T.Nuv.","Líquida"},
                new Color[]{COR_VERDE,COR_VERMELHO,COR_AMARELO,COR_DESTAQUE});
            grafPanel.add(grafSim);
            grafPanel.revalidate(); grafPanel.repaint();
        }
    }

    // ==================== PAINEL ALERTAS ====================
    static class PainelAlertas extends JPanel {
        private DefaultTableModel modelo;
        private JTable tabela;
        private JLabel lblCrit, lblAlto, lblMed, lblRes;

        PainelAlertas() {
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
        }

        void construir() {
            JPanel cab = new JPanel(new BorderLayout());
            cab.setOpaque(false);
            cab.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
            cab.add(Fab.titulo("Central de Alertas",18,COR_TEXTO),BorderLayout.WEST);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
            btns.setOpaque(false);
            JButton btnN = Fab.botao("+ Evento",COR_AMARELO);
            btnN.setPreferredSize(new Dimension(110,36));
            btnN.addActionListener(e->novoEvento());
            JButton btnR = Fab.botao("✓ Resolver",COR_VERDE);
            btnR.setPreferredSize(new Dimension(110,36));
            btnR.addActionListener(e->resolver());
            JButton btnA = Fab.botao("↺ Atualizar",new Color(60,80,120));
            btnA.setPreferredSize(new Dimension(110,36));
            btnA.addActionListener(e->carregar());
            btns.add(btnN); btns.add(btnR); btns.add(btnA);
            cab.add(btns,BorderLayout.EAST);
            add(cab,BorderLayout.NORTH);

            JPanel contadores = new JPanel(new GridLayout(1,4,10,0));
            contadores.setOpaque(false);
            contadores.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));

            String[] nms  = {"CRÍTICO","ALTO","MÉDIO","RESOLVIDO"};
            Color[]  cors = {COR_VERMELHO,COR_LARANJA,COR_AMARELO,COR_VERDE};
            JLabel[] ls   = new JLabel[4];
            for (int i=0;i<4;i++) {
                JPanel c = Fab.painel(COR_PAINEL);
                c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
                c.setBorder(BorderFactory.createEmptyBorder(12,15,12,15));
                JLabel l1=new JLabel(nms[i]);
                l1.setFont(new Font("Segoe UI",Font.PLAIN,11));
                l1.setForeground(cors[i]);
                JLabel l2=new JLabel("0");
                l2.setFont(new Font("Segoe UI",Font.BOLD,24));
                l2.setForeground(COR_TEXTO);
                ls[i]=l2;
                c.add(l1); c.add(l2);
                contadores.add(c);
            }
            lblCrit=ls[0]; lblAlto=ls[1]; lblMed=ls[2]; lblRes=ls[3];
            add(contadores,BorderLayout.NORTH);

            String[] cols = {"ID","Usina","Data/Hora","Tipo",
                             "Descrição","Severidade","Status"};
            modelo = new DefaultTableModel(cols,0) {
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            tabela = new JTable(modelo) {
                @Override public Component prepareRenderer(
                        TableCellRenderer r, int row, int col) {
                    Component c = super.prepareRenderer(r,row,col);
                    String sev = (String)getModel().getValueAt(row,5);
                    String sts = (String)getModel().getValueAt(row,6);
                    if (sts.equals("RESOLVIDO")) {
                        c.setForeground(COR_VERDE);
                    } else {
                        switch(sev) {
                            case "CRITICO": c.setForeground(COR_VERMELHO); break;
                            case "ALTO":    c.setForeground(COR_LARANJA);  break;
                            case "MEDIO":   c.setForeground(COR_AMARELO);  break;
                            default:        c.setForeground(COR_TEXTO);    break;
                        }
                    }
                    if (!isRowSelected(row)) c.setBackground(COR_PAINEL);
                    return c;
                }
            };
            estilizarTabela(tabela);
            carregar();

            JScrollPane sp = new JScrollPane(tabela);
            sp.getViewport().setBackground(COR_PAINEL);
            sp.setBorder(new RoundBorder(COR_GRADE,10,1));
            add(sp,BorderLayout.CENTER);
        }

        void carregar() {
            modelo.setRowCount(0);
            BancoDados bd = BancoDados.get();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
            int crit=0,alto=0,med=0,res=0;
            for (Evento ev : bd.eventos) {
                String nm = bd.usinas.stream()
                    .filter(u->u.id.equals(ev.usinaId))
                    .map(u->u.nome).findFirst().orElse(ev.usinaId);
                modelo.addRow(new Object[]{ev.id,nm,
                    ev.dataHora.format(fmt),ev.tipo,
                    ev.descricao,ev.severidade,ev.status});
                if (ev.status.equals("RESOLVIDO")) res++;
                else switch(ev.severidade) {
                    case "CRITICO": crit++; break;
                    case "ALTO":    alto++; break;
                    case "MEDIO":   med++;  break;
                }
            }
            lblCrit.setText(String.valueOf(crit));
            lblAlto.setText(String.valueOf(alto));
            lblMed.setText(String.valueOf(med));
            lblRes.setText(String.valueOf(res));
        }

        void novoEvento() {
            JDialog d = new JDialog(
                (JFrame)SwingUtilities.getWindowAncestor(this),
                "Novo Evento",true);
            d.setSize(400,320);
            d.setLocationRelativeTo(this);
            d.getContentPane().setBackground(COR_FUNDO);
            d.setLayout(new BorderLayout(10,10));

            JPanel form = new JPanel(new GridLayout(4,2,8,10));
            form.setBackground(COR_PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            List<String> uOpts = BancoDados.get().usinas.stream()
                .map(u->u.id+" - "+u.nome).collect(Collectors.toList());
            JComboBox<String> cUs  = Fab.combo(uOpts.toArray(new String[0]));
            JComboBox<String> cTip = Fab.combo(
                new String[]{"FALHA","ALERTA","INFO","MANUTENCAO"});
            JComboBox<String> cSev = Fab.combo(
                new String[]{"CRITICO","ALTO","MEDIO","BAIXO"});
            JTextField tfDesc = Fab.campo("Descrição");

            form.add(Fab.label("Usina:"));      form.add(cUs);
            form.add(Fab.label("Tipo:"));       form.add(cTip);
            form.add(Fab.label("Severidade:")); form.add(cSev);
            form.add(Fab.label("Descrição:"));  form.add(tfDesc);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.setBackground(COR_FUNDO);
            JButton btnOk  = Fab.botao("Salvar",COR_VERDE);
            JButton btnCan = Fab.botao("Cancelar",new Color(80,80,100));
            btnCan.addActionListener(e->d.dispose());
            btnOk.addActionListener(e -> {
                BancoDados bd = BancoDados.get();
                String nextId = "E"+String.format("%03d",bd.eventos.size()+1);
                String uid2 = ((String)cUs.getSelectedItem()).split(" - ")[0];
                bd.eventos.add(new Evento(nextId,uid2,LocalDateTime.now(),
                    (String)cTip.getSelectedItem(),tfDesc.getText().trim(),
                    (String)cSev.getSelectedItem(),"ATIVO"));
                bd.salvarEventos();
                carregar();
                d.dispose();
            });
            btns.add(btnCan); btns.add(btnOk);
            d.add(form,BorderLayout.CENTER);
            d.add(btns,BorderLayout.SOUTH);
            d.setVisible(true);
        }

        void resolver() {
            int row = tabela.getSelectedRow();
            if (row<0) {
                JOptionPane.showMessageDialog(this,"Selecione um evento.",
                    "Aviso",JOptionPane.WARNING_MESSAGE); return;
            }
            String id = (String)modelo.getValueAt(row,0);
            BancoDados bd = BancoDados.get();
            bd.eventos.stream().filter(e->e.id.equals(id)).findFirst()
                .ifPresent(ev->{ ev.status="RESOLVIDO"; bd.salvarEventos(); carregar(); });
        }
    }

    // ==================== PAINEL USUÁRIOS ====================
    static class PainelUsuarios extends JPanel {
        private DefaultTableModel modelo;
        private JTable tabela;

        PainelUsuarios() {
            setBackground(COR_FUNDO);
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            construir();
        }

        void construir() {
            JPanel cab = new JPanel(new BorderLayout());
            cab.setOpaque(false);
            cab.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
            cab.add(Fab.titulo("Gestão de Usuários",18,COR_TEXTO),BorderLayout.WEST);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
            btns.setOpaque(false);
            JButton btnAdd = Fab.botao("+ Usuário",COR_VERDE);
            btnAdd.setPreferredSize(new Dimension(110,36));
            btnAdd.addActionListener(e->dialogo(null));
            JButton btnDel = Fab.botao("🗑 Excluir",COR_VERMELHO);
            btnDel.setPreferredSize(new Dimension(100,36));
            btnDel.addActionListener(e->excluir());
            btns.add(btnAdd); btns.add(btnDel);
            cab.add(btns,BorderLayout.EAST);
            add(cab,BorderLayout.NORTH);

            String[] cols = {"Login","Perfil"};
            modelo = new DefaultTableModel(cols,0) {
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            tabela = new JTable(modelo);
            estilizarTabela(tabela);
            carregar();

            JScrollPane sp = new JScrollPane(tabela);
            sp.getViewport().setBackground(COR_PAINEL);
            sp.setBorder(new RoundBorder(COR_GRADE,10,1));
            add(sp,BorderLayout.CENTER);

            JPanel info = new JPanel(new GridLayout(1,3,10,0));
            info.setOpaque(false);
            info.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));
            String[][] pInfo = {
                {"👑","MASTER","Acesso total — CRUD completo"},
                {"⚙","OPERADOR","Leitura + criação/edição"},
                {"👁","VISUALIZADOR","Somente leitura"}
            };
            Color[] cors = {COR_VERMELHO,COR_DESTAQUE,COR_VERDE};
            for (int i=0;i<3;i++) {
                JPanel c = Fab.painel(COR_PAINEL);
                c.setLayout(new BorderLayout(8,0));
                c.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
                JLabel ic = new JLabel(pInfo[i][0]);
                ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,28));
                JPanel txt = new JPanel();
                txt.setOpaque(false);
                txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
                JLabel l1=new JLabel(pInfo[i][1]);
                l1.setFont(new Font("Segoe UI",Font.BOLD,13));
                l1.setForeground(cors[i]);
                JLabel l2=new JLabel(pInfo[i][2]);
                l2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                l2.setForeground(COR_TEXTO_SEC);
                txt.add(l1); txt.add(l2);
                c.add(ic,BorderLayout.WEST); c.add(txt,BorderLayout.CENTER);
                info.add(c);
            }
            add(info,BorderLayout.SOUTH);
        }

        void carregar() {
            modelo.setRowCount(0);
            BancoDados.get().usuarios.forEach((login,dados)->
                modelo.addRow(new Object[]{login,dados[1]}));
        }

        void dialogo(String loginEdit) {
            JDialog d = new JDialog(
                (JFrame)SwingUtilities.getWindowAncestor(this),
                loginEdit==null?"Novo Usuário":"Editar Usuário",true);
            d.setSize(360,260);
            d.setLocationRelativeTo(this);
            d.getContentPane().setBackground(COR_FUNDO);
            d.setLayout(new BorderLayout(10,10));

            JPanel form = new JPanel(new GridLayout(3,2,8,12));
            form.setBackground(COR_PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            JTextField tfLogin = Fab.campo(loginEdit!=null?loginEdit:"");
            tfLogin.setEditable(loginEdit==null);
            JPasswordField pfSenha = Fab.senha();
            JComboBox<String> cbP = Fab.combo(
                new String[]{"MASTER","OPERADOR","VISUALIZADOR"});
            if (loginEdit!=null) {
                String[] dados = BancoDados.get().usuarios.get(loginEdit);
                if (dados!=null) cbP.setSelectedItem(dados[1]);
            }

            form.add(Fab.label("Login:"));  form.add(tfLogin);
            form.add(Fab.label("Senha:"));  form.add(pfSenha);
            form.add(Fab.label("Perfil:")); form.add(cbP);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.setBackground(COR_FUNDO);
            JButton btnOk  = Fab.botao("Salvar",COR_VERDE);
            JButton btnCan = Fab.botao("Cancelar",new Color(80,80,100));
            btnCan.addActionListener(e->d.dispose());
            btnOk.addActionListener(e -> {
                String login = tfLogin.getText().trim();
                String senha = new String(pfSenha.getPassword()).trim();
                if (login.isEmpty()||senha.isEmpty()) {
                    JOptionPane.showMessageDialog(d,"Login e senha obrigatórios!",
                        "Erro",JOptionPane.ERROR_MESSAGE); return;
                }
                BancoDados bd = BancoDados.get();
                bd.usuarios.put(login,new String[]{senha,(String)cbP.getSelectedItem()});
                bd.salvarUsuarios();
                carregar();
                d.dispose();
            });
            btns.add(btnCan); btns.add(btnOk);
            d.add(form,BorderLayout.CENTER);
            d.add(btns,BorderLayout.SOUTH);
            d.setVisible(true);
        }

        void excluir() {
            int row = tabela.getSelectedRow();
            if (row<0) {
                JOptionPane.showMessageDialog(this,"Selecione um usuário.",
                    "Aviso",JOptionPane.WARNING_MESSAGE); return;
            }
            String login = (String)modelo.getValueAt(row,0);
            if (login.equals("admin")) {
                JOptionPane.showMessageDialog(this,
                    "Não é possível excluir o admin!","Erro",
                    JOptionPane.ERROR_MESSAGE); return;
            }
            if (JOptionPane.showConfirmDialog(this,"Excluir "+login+"?",
                    "Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                BancoDados.get().usuarios.remove(login);
                BancoDados.get().salvarUsuarios();
                carregar();
            }
        }
    }

    // ==================== UTILITÁRIOS ====================
    static void estilizarTabela(JTable t) {
        t.setBackground(COR_PAINEL);
        t.setForeground(COR_TEXTO);
        t.setFont(new Font("Segoe UI",Font.PLAIN,13));
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,1));
        t.setSelectionBackground(new Color(0,80,160,180));
        t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setBackground(new Color(18,26,42));
        t.getTableHeader().setForeground(COR_DESTAQUE);
        t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        t.getTableHeader().setPreferredSize(new Dimension(0,36));
        t.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0,0,1,0,COR_GRADE));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tb,v,sel,foc,r,c);
                setBackground(sel ? new Color(0,80,160,160) :
                    (r%2==0 ? COR_PAINEL : COR_PAINEL2));
                setForeground(sel ? Color.WHITE : COR_TEXTO);
                setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return this;
            }
        });
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("OptionPane.background", COR_FUNDO);
        UIManager.put("Panel.background", COR_FUNDO);
        UIManager.put("OptionPane.messageForeground", COR_TEXTO);

        BancoDados.get();
        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
