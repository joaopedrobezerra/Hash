# 📊 Análise de Performance de Tabelas Hash

## 📋 Sobre o Projeto

Este projeto implementa e analisa o desempenho de diferentes estratégias de resolução de colisões em tabelas hash utilizando Java. O objetivo é comparar três abordagens distintas: **Encadeamento Separado (Separate Chaining)**, **Endereçamento Aberto com Sondagem Linear** e **Endereçamento Aberto com Hash Duplo**.

---

## 🎯 Objetivos

1. Implementar três métodos diferentes de tabelas hash
2. Avaliar desempenho em diferentes escalas (100K, 1M e 10M elementos)
3. Testar com diferentes tamanhos de tabela (M = 1.000, 10.000, 100.000)
4. Medir tempo de inserção e busca
5. Analisar colisões e distribuição de dados
6. Comparar eficiência através de gráficos e tabelas

---

## 🏗️ Estrutura do Projeto

```
Hash/
├── src/
│   ├── core/
│   │   ├── HashTable.java          # Interface base para todas as implementações
│   │   └── Metrics.java             # Classe para armazenar métricas de desempenho
│   ├── impl/
│   │   ├── HashSeparateChaining.java           # Implementação com encadeamento
│   │   ├── HashOpenAddressingLinear.java       # Implementação com sondagem linear
│   │   └── HashOpenAddressingDouble.java       # Implementação com hash duplo
│   ├── model/
│   │   └── Registro.java            # Modelo de dados (9 dígitos)
│   ├── util/
│   │   ├── DataGen.java             # Gerador de datasets com seeds fixas
│   │   └── Reporter.java            # Exportador de resultados para CSV
│   └── MainRunner.java              # Classe principal de execução
├── datasets/                        # Datasets gerados (não versionados)
├── resultados.csv                   # Resultados das execuções
├── analyze_results.py               # Script Python para gerar gráficos
└── requirements.txt                 # Dependências Python
```

---

## 🚀 Como Executar

### Pré-requisitos

- Java JDK 11 ou superior
- Python 3.8+ (para análise de resultados)
- pip (gerenciador de pacotes Python)

### Passo 1: Compilar e Executar o Programa Java

```bash
# Navegar até a pasta src
cd src

# Compilar todos os arquivos
javac -d ../bin core/*.java impl/*.java model/*.java util/*.java MainRunner.java

# Executar o programa
cd ..
java -cp bin MainRunner
```

### Passo 2: Gerar Gráficos

```bash
# Instalar dependências Python
pip install -r requirements.txt

# Executar script de análise
python analyze_results.py
```

---

## 📊 Metodologia

### Configuração dos Testes

| Parâmetro | Valores |
|-----------|---------|
| **Tamanhos de Tabela (M)** | 1.000, 10.000, 100.000 |
| **Tamanhos de Dataset (N)** | 100.000, 1.000.000, 10.000.000 |
| **Seeds Fixas** | 42, 43, 44 (para reprodutibilidade) |
| **Métodos Testados** | Encadeamento, Linear, Duplo |

### Métricas Coletadas

1. **Tempo de Inserção** (ms)
2. **Tempo de Busca** (ms)
3. **Colisões na Inserção**
4. **Colisões na Busca**
5. **Top 3 Maiores Listas** (apenas Encadeamento)
6. **Gap Mínimo/Máximo/Médio** (apenas Open Addressing)

---


## 👥 Autores

**Curso**: Resolução de Problemas Estruturados em Computação  
**Instituição**: Pontificia Universidade Catolica do Parana
**Período**: 4º Período  
**Alunos**: Joao Pedro Bezerra e Gabriel Zem Muraro

---


