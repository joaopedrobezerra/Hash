import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# Configurar estilo dos gráficos
plt.style.use('seaborn-v0_8')
sns.set_palette("husl")

# PASSO 3: Ler os dados do CSV
print("📊 Lendo dados do CSV...")
df = pd.read_csv('resultados.csv')

# Verificar se os dados foram lidos corretamente
print("✅ Dados carregados com sucesso!")
print(f"📈 Total de registros: {len(df)}")
print("\n📋 Primeiras linhas:")
print(df.head())

# PASSO 4: Análise exploratória
print("\n🔍 Análise dos dados:")
print(df.describe())

# PASSO 5: Criar gráficos
print("\n📊 Criando gráficos...")

# Configurar figura com subplots
fig, axes = plt.subplots(2, 3, figsize=(18, 12))
fig.suptitle('Análise de Performance - Tabelas Hash', fontsize=16, fontweight='bold')

# Gráfico 1: Tempo de Inserção vs Tamanho do Dataset
ax1 = axes[0, 0]
for metodo in df['metodo'].unique():
    data = df[df['metodo'] == metodo]
    ax1.plot(data['n'], data['insert_ms'], 'o-', label=metodo, linewidth=2, markersize=6)
ax1.set_xlabel('Tamanho do Dataset')
ax1.set_ylabel('Tempo de Inserção (ms)')
ax1.set_title('Tempo de Inserção vs Dataset Size')
ax1.legend()
ax1.set_yscale('log')
ax1.grid(True, alpha=0.3)

# Gráfico 2: Tempo de Busca vs Tamanho do Dataset
ax2 = axes[0, 1]
for metodo in df['metodo'].unique():
    data = df[df['metodo'] == metodo]
    ax2.plot(data['n'], data['search_ms'], 's-', label=metodo, linewidth=2, markersize=6)
ax2.set_xlabel('Tamanho do Dataset')
ax2.set_ylabel('Tempo de Busca (ms)')
ax2.set_title('Tempo de Busca vs Dataset Size')
ax2.legend()
ax2.set_yscale('log')
ax2.grid(True, alpha=0.3)

# Gráfico 3: Colisões de Inserção
ax3 = axes[0, 2]
for metodo in df['metodo'].unique():
    data = df[df['metodo'] == metodo]
    ax3.plot(data['n'], data['collisions_insert'], '^-', label=metodo, linewidth=2, markersize=6)
ax3.set_xlabel('Tamanho do Dataset')
ax3.set_ylabel('Colisões de Inserção')
ax3.set_title('Colisões de Inserção vs Dataset Size')
ax3.legend()
ax3.set_yscale('log')
ax3.grid(True, alpha=0.3)

# Gráfico 4: Comparação por Tamanho da Tabela (M)
ax4 = axes[1, 0]
pivot_insert = df.pivot_table(values='insert_ms', index='M', columns='metodo', aggfunc='mean')
pivot_insert.plot(kind='bar', ax=ax4, width=0.8)
ax4.set_xlabel('Tamanho da Tabela (M)')
ax4.set_ylabel('Tempo de Inserção (ms)')
ax4.set_title('Performance por Tamanho da Tabela')
ax4.legend(title='Método')
ax4.set_yscale('log')
ax4.grid(True, alpha=0.3)

# Gráfico 5: Top 3 Maiores Listas (Encadeamento)
ax5 = axes[1, 1]
encadeamento = df[df['metodo'] == 'Encadeamento']
if not encadeamento.empty:
    ax5.bar(['Top 1', 'Top 2', 'Top 3'], 
            [encadeamento['top1chain'].iloc[0], 
             encadeamento['top2chain'].iloc[0], 
             encadeamento['top3chain'].iloc[0]], 
            color=['red', 'orange', 'yellow'])
    ax5.set_ylabel('Tamanho da Lista')
    ax5.set_title('Top 3 Maiores Listas (Encadeamento)')
    ax5.grid(True, alpha=0.3)

# Gráfico 6: Gaps (Linear e Duplo)
ax6 = axes[1, 2]
for metodo in ['Linear', 'Duplo']:
    data = df[df['metodo'] == metodo]
    if not data.empty:
        ax6.plot(data['n'], data['gap_avg'], 'o-', label=f'{metodo} - Gap Médio', linewidth=2)
ax6.set_xlabel('Tamanho do Dataset')
ax6.set_ylabel('Gap Médio')
ax6.set_title('Análise de Gaps (Linear/Duplo)')
ax6.legend()
ax6.grid(True, alpha=0.3)

# Ajustar layout
plt.tight_layout()

# PASSO 6: Salvar gráficos
print("💾 Salvando gráficos...")
plt.savefig('hash_performance_analysis.png', dpi=300, bbox_inches='tight')
plt.savefig('hash_performance_analysis.pdf', bbox_inches='tight')

# Mostrar gráfico
plt.show()

# PASSO 7: Análise estatística
print("\n📈 ANÁLISE ESTATÍSTICA:")
print("=" * 50)

# Melhor método por categoria
print("\n🏆 MELHOR MÉTODO POR CATEGORIA:")
print("-" * 30)

# Tempo de inserção
best_insert = df.loc[df.groupby('n')['insert_ms'].idxmin()]
print("\n⏱️ Menor tempo de inserção por dataset:")
for _, row in best_insert.iterrows():
    print(f"  {row['n']:,} elementos: {row['metodo']} ({row['insert_ms']} ms)")

# Tempo de busca
best_search = df.loc[df.groupby('n')['search_ms'].idxmin()]
print("\n🔍 Menor tempo de busca por dataset:")
for _, row in best_search.iterrows():
    print(f"  {row['n']:,} elementos: {row['metodo']} ({row['search_ms']} ms)")

# Menos colisões
best_collisions = df.loc[df.groupby('n')['collisions_insert'].idxmin()]
print("\n💥 Menos colisões por dataset:")
for _, row in best_collisions.iterrows():
    print(f"  {row['n']:,} elementos: {row['metodo']} ({row['collisions_insert']:,} colisões)")

# PASSO 8: Salvar relatório em texto
print("\n📝 Gerando relatório...")
with open('relatorio_analise.txt', 'w', encoding='utf-8') as f:
    f.write("RELATÓRIO DE ANÁLISE - TABELAS HASH\n")
    f.write("=" * 50 + "\n\n")
    
    f.write("RESUMO DOS DADOS:\n")
    f.write(f"Total de testes: {len(df)}\n")
    f.write(f"Métodos testados: {', '.join(df['metodo'].unique())}\n")
    f.write(f"Tamanhos de tabela: {', '.join(map(str, df['M'].unique()))}\n")
    f.write(f"Tamanhos de dataset: {', '.join(map(str, df['n'].unique()))}\n\n")
    
    f.write("MELHORES RESULTADOS:\n")
    f.write("-" * 20 + "\n")
    
    for _, row in best_insert.iterrows():
        f.write(f"Menor tempo inserção ({row['n']:,}): {row['metodo']} - {row['insert_ms']} ms\n")
    
    for _, row in best_search.iterrows():
        f.write(f"Menor tempo busca ({row['n']:,}): {row['metodo']} - {row['search_ms']} ms\n")

print("✅ Análise concluída!")
print("📁 Arquivos gerados:")
print("  - hash_performance_analysis.png")
print("  - hash_performance_analysis.pdf") 
print("  - relatorio_analise.txt")