package org.example.listly;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane backgroundPane;

    @FXML
    private HBox topBar;
    @FXML
    private VBox sortBox;

    @FXML
    private Label tvHome;
    @FXML
    private Label tvSort;
    @FXML
    private Label tvSort2;

    @FXML
    private ListView<CategoryItem> homeRecycler;

    @FXML
    private ImageView rm;
    @FXML
    private ImageView stopwatchBtn;
    @FXML
    private ImageView imagebutton66;
    @FXML
    private ImageView imageView2;

    private final List<CategoryItem> categories = new ArrayList<>();
    private SortMode sortMode = SortMode.ALPHABETICAL;

    // Image cache for fast loading
    private final Map<String, Image> imageCache = new HashMap<>();

    private static final String PREF_BG = "main_bg";
    private static final String PREF_USERNAME = "username";

    private final String[] THEMES = new String[] {
            "p10.jpg", "p1.jpg", "p2.jpg", "p3.jpg",
            "p4.jpg", "p5.jpg", "p6.jpg", "p7.jpg",
            "p8.jpg", "p9.jpg", "g0.jpg", "g9.jpg"
    };

    @FXML
    private void initialize() {
        applyBackground("/images/p6.jpg");
        setIcon(rm, "/images/alarmclock.jpeg");
        setIcon(stopwatchBtn, "/images/stopwatch.jpeg");
        setIcon(imagebutton66, "/images/add2.jpg");
        setIcon(imageView2, "/images/more.png");

        tvSort2.setText("All Lists");
        tvHome.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (!homeRecycler.getItems().isEmpty()) {
                homeRecycler.scrollTo(0);
            }
        });

        stopwatchBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> openStopwatch());
        rm.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> openReminders());
        imageView2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showMoreDialog());
        imagebutton66.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showAddListStyleDialog());
        tvSort2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showSortDialog());

        // Apply CSS to hide scrollbars
        var cssUrl = getClass().getResource("hide-scrollbars.css");
        if (cssUrl != null) {
            homeRecycler.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Preload all theme images for instant display
        preloadThemeImages();

        homeRecycler.setCellFactory(list -> new CategoryCell());
        loadCategories();
    }

    private void preloadThemeImages() {
        // Load all theme images synchronously for instant display
        System.out.println("Starting theme image preload...");
        int loaded = 0;
        for (String theme : THEMES) {
            String imagePath = "/images/" + theme;
            var imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                try {
                    // Load image with exact preview size, NO background loading for instant
                    // availability
                    Image img = new Image(imageUrl.toExternalForm(), 230, 285, false, true, false);

                    // Wait for image to be fully loaded before caching
                    if (!img.isError()) {
                        imageCache.put(theme, img);
                        loaded++;
                        System.out.println(
                                "✓ Loaded: " + theme + " (" + (int) img.getWidth() + "x" + (int) img.getHeight() + ")");
                    } else {
                        System.err.println("✗ Error loading: " + theme);
                    }
                } catch (Exception e) {
                    System.err.println("✗ Exception loading " + theme + ": " + e.getMessage());
                }
            } else {
                System.err.println("✗ Image not found: " + imagePath);
            }
        }
        System.out.println("Successfully preloaded " + loaded + "/" + THEMES.length + " theme images");
    }

    private void applyBackground(String resourcePath) {
        var url = MainController.class.getResource(resourcePath);
        if (url == null)
            return;
        Image img = new Image(url.toExternalForm());
        BackgroundSize phoneSize = new BackgroundSize(
                1.0, 1.0,
                true, true,
                false, true);
        BackgroundImage phoneBg = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                phoneSize);
        backgroundPane.setBackground(new Background(phoneBg));
    }

    private void setIcon(ImageView view, String resourcePath) {
        var url = MainController.class.getResource(resourcePath);
        if (url != null) {
            view.setImage(new Image(url.toExternalForm()));
        }
    }

    private void openStopwatch() {
        switchContent("stopwatch.fxml");
    }

    private void openReminders() {
        switchContent("reminders.fxml");
    }

    private void openCheckboxScreen(ListItem item) {
        try {
            System.out.println("Opening list: " + item.title + " (ID: " + item.id + ", Style: " + item.style + ")");
            // Always use checkboxscreen.fxml for all list types
            FXMLLoader loader = new FXMLLoader(getClass().getResource("checkboxscreen.fxml"));
            AnchorPane pane = loader.load();
            CheckboxscreenController c = loader.getController();
            c.setListItem(item);
            backgroundPane.getChildren().setAll(pane);
            System.out.println("Successfully opened list: " + item.title);
        } catch (Exception e) {
            System.err.println("Error opening list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchContent(String fxml) {
        try {
            AnchorPane pane = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxml)));
            backgroundPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        List<CategoryItem> loaded = CategoryDao.loadAll();
        categories.clear();
        if (loaded != null) {
            categories.addAll(loaded);
        }
        for (CategoryItem c : categories) {
            long catTotal = 0;
            if (c.lists != null) {
                for (ListItem li : c.lists) {
                    catTotal += li.totalDurationMs;
                }
            }
            c.totalDurationMs = catTotal;
        }
        applySort();
    }

    private void saveCategories() {
        CategoryDao.saveAll(categories);
    }

    private void showMoreDialog() {
        List<String> opts = List.of("Change Background Theme", "Set User Name");
        int choice = Dialogs.choice("More Options", opts);
        if (choice == 0)
            showThemeDialog();
        if (choice == 1)
            showUsernameDialog();
    }

    private void showThemeDialog() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < THEMES.length; i++)
            names.add("Theme " + (i + 1));
        int which = Dialogs.choice("Select Background Theme", names);
        if (which >= 0) {
            applyBackground("/images/p6.jpg");
        }
    }

    private void showUsernameDialog() {
        String current = Prefs.get(PREF_USERNAME, "");
        String name = Dialogs.input("Set User Name", "Enter user name", current);
        if (name != null) {
            Prefs.put(PREF_USERNAME, name);
        }
    }

    private void showSortDialog() {
        String[] options = {
                "Alphabetical",
                "Recent",
                "Creation Time (Oldest First)",
                "List Style"
        };
        int i = Dialogs.choice("Sort Lists", Arrays.asList(options));
        if (i < 0)
            return;
        if (i == 0)
            sortMode = SortMode.ALPHABETICAL;
        if (i == 1)
            sortMode = SortMode.RECENT;
        if (i == 2)
            sortMode = SortMode.OLDEST;
        if (i == 3)
            sortMode = SortMode.STYLE;
        tvSort2.setText(options[i]);
        applySort();
    }

    private void applySort() {
        if (sortMode == SortMode.ALPHABETICAL) {
            categories.sort((a, b) -> a.name.toLowerCase().compareTo(b.name.toLowerCase()));
            for (CategoryItem c : categories)
                ListSorter.sortListsAlphabetical(c.lists);
        } else if (sortMode == SortMode.RECENT) {
            categories.sort((a, b) -> Long.compare(b.createdAt, a.createdAt));
            for (CategoryItem c : categories)
                ListSorter.sortListsNewest(c.lists);
        } else if (sortMode == SortMode.OLDEST) {
            categories.sort((a, b) -> Long.compare(a.createdAt, b.createdAt));
            for (CategoryItem c : categories)
                ListSorter.sortListsOldest(c.lists);
        } else if (sortMode == SortMode.STYLE) {
            categories.sort((a, b) -> a.name.toLowerCase().compareTo(b.name.toLowerCase()));
            for (CategoryItem c : categories)
                ListSorter.sortListsByStyle(c.lists);
        }
        homeRecycler.getItems().setAll(categories);
    }

    private void showAddListStyleDialog() {
        String[] options = { "Checkbox", "Wishlist", "Plain List", "Notes", "Memo" };
        int i = Dialogs.choice("Select List Type", Arrays.asList(options));
        if (i < 0)
            return;
        ListStyle selected;
        if (i == 0)
            selected = ListStyle.CHECKBOX;
        else if (i == 1)
            selected = ListStyle.WISHLIST;
        else if (i == 2)
            selected = ListStyle.PLAIN;
        else if (i == 3)
            selected = ListStyle.NOTE;
        else
            selected = ListStyle.MEMO;
        showCategoryChoiceDialog(selected);
    }

    private void showCategoryChoiceDialog(ListStyle style) {
        List<String> names = categories.stream().map(c -> c.name).collect(Collectors.toList());
        names.add("New Category");
        int i = Dialogs.choice("Choose Category", names);
        if (i < 0)
            return;
        if (i == names.size() - 1)
            showCreateCategoryDialog(style);
        else
            showCreateListDialog(categories.get(i), style);
    }

    private void showCreateCategoryDialog(ListStyle style) {
        String name = Dialogs.input("New Category", "Category name", "");
        if (name == null || name.trim().isEmpty())
            return;
        long now = System.currentTimeMillis();
        CategoryItem cat = new CategoryItem(UUID.randomUUID().toString(), name);
        cat.createdAt = now;
        cat.updatedAt = now;
        categories.add(cat);
        saveCategories();
        applySort();
        showCreateListDialog(cat, style);
    }

    private void showCreateListDialog(CategoryItem category, ListStyle style) {
        String title = Dialogs.input("New " + style.name() + " List", "List name", "");
        if (title == null || title.trim().isEmpty())
            return;
        long now = System.currentTimeMillis();
        ListItem item = new ListItem();
        item.id = UUID.randomUUID().toString();
        item.categoryId = category.id;
        item.title = title;
        item.prefKey = "list_" + UUID.randomUUID();
        item.style = style;
        item.themeImage = "p5.jpg";
        item.textColor = 0xFF030320;
        item.fontSizeSp = 16;
        item.fontStyle = "NORMAL";
        item.createdAt = now;
        item.updatedAt = now;
        item.totalDurationMs = 0;
        category.lists.add(item);
        category.updatedAt = now;
        saveCategories();
        applySort();
    }

    private class CategoryCell extends ListCell<CategoryItem> {
        private AnchorPane root;
        private Label tvCategory;
        private ListView<RowItem> rvRows;
        private long lastClick = 0;

        CategoryCell() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("homebox.fxml"));
                root = loader.load();
                tvCategory = (Label) root.lookup("#tvCategory");
                rvRows = (ListView<RowItem>) root.lookup("#rvRows");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(CategoryItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || root == null) {
                setGraphic(null);
                return;
            }
            tvCategory.setText(item.name);
            tvCategory.setOnMouseClicked(e -> {
                long now = System.currentTimeMillis();
                if (now - lastClick < 300)
                    return;
                showCategoryOptions(item);
                lastClick = now;
            });

            RowItem rowItem = new RowItem();
            rowItem.lists = item.lists;
            rvRows.getItems().setAll(rowItem);
            rvRows.setCellFactory(list -> new RowCell());
            rvRows.setFixedCellSize(330); // Fixed height for smooth scrolling

            // Apply CSS to hide scrollbars
            var cssUrl = getClass().getResource("hide-scrollbars.css");
            if (cssUrl != null) {
                rvRows.getStylesheets().add(cssUrl.toExternalForm());
            }

            setGraphic(root);
        }

        private void showCategoryOptions(CategoryItem cat) {
            String[] options = { "Edit Category Name", "Delete Category", "Duration" };
            int i = Dialogs.choice("Category Options", Arrays.asList(options));
            if (i == 0)
                editCategory(cat);
            else if (i == 1)
                deleteCategory(cat);
            else if (i == 2)
                showCategoryDuration(cat);
        }

        private void editCategory(CategoryItem cat) {
            String name = Dialogs.input("Edit Category", "Name", cat.name);
            if (name == null || name.trim().isEmpty())
                return;
            cat.name = name.trim();
            cat.updatedAt = System.currentTimeMillis();
            saveCategories();
            applySort();
        }

        private void deleteCategory(CategoryItem cat) {
            boolean ok = Dialogs.confirm("Delete Category?", "All lists inside this category will be deleted.");
            if (!ok)
                return;
            categories.remove(cat);
            saveCategories();
            applySort();
        }

        private void showCategoryDuration(CategoryItem cat) {
            long createdAt = cat.createdAt != 0 ? cat.createdAt : System.currentTimeMillis();
            long now = System.currentTimeMillis();
            long duration = now - createdAt;
            String created = Dates.formatDateTime(createdAt);
            String msg = "Created: " + created + "\nElapsed: " + Dates.formatDuration(duration);
            Dialogs.info("Category Info", msg);
        }
    }

    private class RowCell extends ListCell<RowItem> {
        private AnchorPane root;
        private ListView<ListItem> rvHorizontalLists;

        RowCell() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item_row.fxml"));
                root = loader.load();
                rvHorizontalLists = (ListView<ListItem>) root.lookup("#rvHorizontalLists");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(RowItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || root == null) {
                setGraphic(null);
                return;
            }
            rvHorizontalLists.getItems().setAll(item.lists);
            rvHorizontalLists.setCellFactory(list -> new ListCellImpl());
            rvHorizontalLists.setFixedCellSize(250); // Fixed width for each cell in horizontal list

            // Apply CSS to hide scrollbars
            var cssUrl = getClass().getResource("hide-scrollbars.css");
            if (cssUrl != null && !rvHorizontalLists.getStylesheets().contains(cssUrl.toExternalForm())) {
                rvHorizontalLists.getStylesheets().add(cssUrl.toExternalForm());
            }

            setGraphic(root);
        }
    }

    private class ListCellImpl extends ListCell<ListItem> {
        private VBox root;
        private Label tvListTitle;
        private StackPane previewContainer;
        private long lastClick = 0;

        ListCellImpl() {
            // Create UI programmatically for horizontal layout
            root = new VBox(5);
            root.setPrefWidth(240);
            root.setPrefHeight(320);
            root.setPadding(new javafx.geometry.Insets(5));
            root.setStyle("-fx-background-color: transparent;");

            tvListTitle = new Label();
            tvListTitle.setStyle("-fx-text-fill: #030320; -fx-font-size: 16;");
            tvListTitle.setWrapText(false);
            tvListTitle.setMaxWidth(230);
            tvListTitle.setPrefHeight(25);

            previewContainer = new StackPane();
            previewContainer.setPrefWidth(230);
            previewContainer.setPrefHeight(285);
            previewContainer.setMaxWidth(230);
            previewContainer.setMaxHeight(285);
            previewContainer.setStyle(
                    "-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

            root.getChildren().addAll(tvListTitle, previewContainer);
        }

        @Override
        protected void updateItem(ListItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }

            tvListTitle.setText(item.title);
            tvListTitle.setStyle("-fx-text-fill:#030320; -fx-font-size:" + item.fontSizeSp + ";");
            if ("BOLD".equals(item.fontStyle)) {
                tvListTitle.setStyle(tvListTitle.getStyle() + "-fx-font-weight:bold;");
            } else if ("ITALIC".equals(item.fontStyle)) {
                tvListTitle.setStyle(tvListTitle.getStyle() + "-fx-font-style:italic;");
            }

            // Load and display theme image as background
            previewContainer.getChildren().clear();
            previewContainer.setStyle(
                    "-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

            // Use cached image for instant display
            Image themeImage = imageCache.get(item.themeImage);

            // If not in cache, load it now
            if (themeImage == null) {
                String imagePath = "/images/" + item.themeImage;
                var imageUrl = MainController.class.getResource(imagePath);
                if (imageUrl != null) {
                    themeImage = new Image(imageUrl.toExternalForm(), 230, 285, false, true, true);
                    imageCache.put(item.themeImage, themeImage);
                }
            }

            if (themeImage != null) {
                // Cover the entire preview container with the image
                BackgroundSize bgSize = new BackgroundSize(
                        230, 285,
                        false, false,
                        false, true);
                BackgroundImage bgImage = new BackgroundImage(
                        themeImage,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        bgSize);
                previewContainer.setBackground(new Background(bgImage));
            } else {
                // Fallback: show a colored background if image fails
                previewContainer.setStyle(previewContainer.getStyle() +
                        "-fx-background-color: #4A90E2;");
            }

            // Preview shows ONLY the theme image - no content overlay (like Android app)

            // Click on preview container to open the list
            previewContainer.setOnMouseClicked(e -> {
                System.out.println("Preview clicked for: " + item.title);
                e.consume();
                openCheckboxScreen(item);
            });

            // Click on entire card to open the list (except title)
            root.setOnMouseClicked(e -> {
                System.out.println("Card clicked for: " + item.title);
                openCheckboxScreen(item);
            });

            // Double-click on title to show options
            tvListTitle.setOnMouseClicked(e -> {
                e.consume(); // Always consume title clicks
                long now = System.currentTimeMillis();
                if (now - lastClick < 300) {
                    System.out.println("Title double-clicked for: " + item.title);
                    showOptions(item);
                } else {
                    System.out.println("Title single-clicked for: " + item.title);
                }
                lastClick = now;
            });
            setGraphic(root);
        }

        private void showOptions(ListItem item) {
            String[] options = {
                    "Edit Name", "Change Theme",
                    "Text Color", "Font Style/Size",
                    "Delete", "Duration"
            };
            int i = Dialogs.choice("List Options", Arrays.asList(options));
            if (i == 0)
                editName(item);
            else if (i == 1)
                pickTheme(item);
            else if (i == 2)
                pickTextColor(item);
            else if (i == 3)
                pickFont(item);
            else if (i == 4)
                deleteList(item);
            else if (i == 5)
                showListDuration(item);
        }

        private void editName(ListItem item) {
            String name = Dialogs.input("Edit List Name", "Name", item.title);
            if (name == null || name.trim().isEmpty())
                return;
            item.title = name.trim();
            item.updatedAt = System.currentTimeMillis();
            saveCategories();
            applySort();
        }

        private void pickTheme(ListItem item) {
            List<String> names = new ArrayList<>();
            for (int i = 0; i < THEMES.length; i++)
                names.add("Theme " + (i + 1));
            int which = Dialogs.choice("Select Theme", names);
            if (which < 0)
                return;
            item.themeImage = THEMES[which];
            item.updatedAt = System.currentTimeMillis();
            saveCategories();
            applySort();
        }

        private void pickTextColor(ListItem item) {
            String[] options = { "Default", "Red", "Blue", "Green" };
            int[] colors = {
                    0xFF030320,
                    0xFFFF0000,
                    0xFF0000FF,
                    0xFF008000
            };
            int i = Dialogs.choice("Select Text Color", Arrays.asList(options));
            if (i < 0)
                return;
            item.textColor = colors[i];
            item.updatedAt = System.currentTimeMillis();
            saveCategories();
            applySort();
        }

        private void pickFont(ListItem item) {
            String[] styles = { "Normal", "Bold", "Italic" };
            int styleIndex = Dialogs.choice("Font Style", Arrays.asList(styles));
            if (styleIndex < 0)
                return;
            String style;
            if (styleIndex == 1)
                style = "BOLD";
            else if (styleIndex == 2)
                style = "ITALIC";
            else
                style = "NORMAL";
            String sizeStr = Dialogs.input("Font Size", "Font size (px)", String.valueOf(item.fontSizeSp));
            float sz;
            try {
                sz = Float.parseFloat(sizeStr);
            } catch (Exception e) {
                sz = 16;
            }
            item.fontStyle = style;
            item.fontSizeSp = sz;
            item.updatedAt = System.currentTimeMillis();
            saveCategories();
            applySort();
        }

        private void deleteList(ListItem item) {
            for (CategoryItem c : categories) {
                if (c.lists.remove(item))
                    break;
            }
            saveCategories();
            applySort();
        }

        private void showListDuration(ListItem item) {
            long createdAt = item.createdAt != 0 ? item.createdAt : System.currentTimeMillis();
            long now = System.currentTimeMillis();
            long duration = now - createdAt;
            String created = Dates.formatDateTime(createdAt);
            String msg = "Created: " + created + "\nDuration: " + Dates.formatDuration(duration);
            Dialogs.info("List Info", msg);
        }
    }
}
