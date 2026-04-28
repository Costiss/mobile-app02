- Gabriel Costa Martins Ganassin - GRR 20204482
- Gabriel Jaremczyk Marques - GRR 20250168
- Nilmar Pierin - GRR 20204501
- Mario Morais Neto - GRR20204494

https://github.com/Costiss/mobile-app02

## Descrição do Projeto

Aplicativo de gerenciamento de biblioteca de música, podendo cadastrar, avaliar e organizar suas músicas

## Regras de negócios:

- Avaliar as músicas com uma nota de 1 a 5 estrelas
- Adicionar músicas aos favoritos
- Criar uma lista de "Para Ouvir Depois" para organizar as músicas que deseja ouvir

- Ordenação das músicas pode ser feita por titulo, artista, ano, avaliação ou genero
- Filtros de dados disponíveis: titulo, artista, ano, avaliação, genero, favoritos e para ouvir depois/já ouvidas

## Estrutura do Projeto

```
  app/src/main/
  ├── java/br/ufpr/musicbookmark/
  │   ├── MainActivity.kt               # Tela principal com lista de músicas
  │   ├── MusicDetailActivity.kt        # Tela de cadastro/edição de música
  │   ├── adapter/
  │   │   └── MusicAdapter.kt           # Adaptador da ListView de músicas
  │   ├── controller/
  │   │   └── MusicController.kt        # Lógica de negócio: busca, ordenação, validação
  │   ├── database/
  │   │   ├── DBHelper.kt               # Inicialização e schema do banco SQLite
  │   │   └── MusicDAO.kt               # Operações CRUD no banco de dados
  │   └── model/
  │       └── Music.kt                  # Data class com os campos da música
  └── res/
      ├── layout/
      │   ├── activity_main.xml         # Layout da tela principal
      │   ├── activity_music_detail.xml # Layout da tela de detalhe
      │   ├── list_item_music.xml       # Layout de cada item da lista
      │   └── spinner_item_dark.xml     # Item customizado de spinner (tema escuro)
      ├── drawable/                     # Ícones: add, delete, save, favorite, badge
      └── values/                       # colors, strings, arrays, themes
```

## Telas desenvolvidas

### Tela 1 — Lista de Músicas (MainActivity)

Listagem, ordenação e filtros das musicas já cadastradas, assim como o botão que direciona para a tela de cadastro/edição de música
Clicar em uma música leva para a tela de cadastro/edição, onde é possível editar ou excluir a música selecionada

### Tela 2 — Cadastro / Edição de Música (MusicDetailActivity)

Tela única de cadastro e edição de música

## Fluxo de navegação

- Salvar música → insere ou atualiza no banco e retorna à lista
- Editar música → exibe os dados da música selecionada para edição
- Editar música -> Excluir música → exibe confirmação, remove do banco e retorna à lista
- Voltar com alterações → exibe diálogo "Descartar alterações?" antes de sair
- Filtro por favoritos → exibe apenas as músicas marcadas como favoritas
- Filtro por status → exibe todas, apenas "Para Ouvir" ou apenas "Ouvidas"
- Busca por campo aberto → exibe músicas que correspondem ao termo de busca
- Ordenação → exibe opções de ordenação (título, artista, ano, avaliação, gênero) e ordena a lista
