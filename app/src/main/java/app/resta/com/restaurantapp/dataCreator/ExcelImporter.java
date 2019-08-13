package app.resta.com.restaurantapp.dataCreator;

import android.content.res.AssetManager;
import android.widget.Toast;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantMetadata;

public class ExcelImporter {
    private IngredientAdminDaoI ingredientAdminDao;
    private TagAdminDaoI tagAdminDao;
    private MenuItemAdminDaoI menuItemAdminDao;

    public ExcelImporter() {
        ingredientAdminDao = new IngredientAdminFireStoreDao();
        tagAdminDao = new TagAdminFireStoreDao();
        menuItemAdminDao = new MenuItemAdminFireStoreDao();
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
    }

    public void importIngredientsData() {
        try {
            AssetManager assetManager = MyApplication.getAppContext().getAssets();
            InputStream dataInputStream = assetManager.open(RestaurantMetadata.getRestaurantId() + ".xlsx");
            Workbook workbook = WorkbookFactory.create(dataInputStream);
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            System.out.println("Retrieving Sheets using for-each loop");
            for (final Sheet sheet : workbook) {
                if (sheet.getSheetName().equalsIgnoreCase("Ingredients")) {
                    ingredientAdminDao.getIngredients(new OnResultListener<List<Ingredient>>() {
                        @Override
                        public void onCallback(List<Ingredient> ingredients) {
                            Map<String, String> ingredientMap = new HashMap<>();
                            for (Ingredient ingredient : ingredients) {
                                ingredientMap.put(ingredient.getName().toLowerCase(), ingredient.getId());
                            }
                            importIngredientsData(sheet, ingredientMap);
                        }
                    });
                }
            }
            workbook.close();
        } catch (IOException e) {
            Toast.makeText(MyApplication.getAppContext(), "There is not file present for this restautant. Please contact support.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MyApplication.getAppContext(), "Error when importing the data", Toast.LENGTH_LONG).show();
        }
    }


    public void importTagsData() {
        try {
            AssetManager assetManager = MyApplication.getAppContext().getAssets();
            InputStream dataInputStream = assetManager.open("1.xlsx");
            Workbook workbook = WorkbookFactory.create(dataInputStream);
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            System.out.println("Retrieving Sheets using for-each loop");
            for (final Sheet sheet : workbook) {
                if (sheet.getSheetName().equalsIgnoreCase("Tags")) {
                    tagAdminDao.getTags(new OnResultListener<List<Tag>>() {
                        @Override
                        public void onCallback(List<Tag> tags) {
                            Map<String, String> tagMap = new HashMap<>();
                            for (Tag Tag : tags) {
                                tagMap.put(Tag.getName().toLowerCase(), Tag.getId());
                            }
                            importTagsData(sheet, tagMap);
                        }
                    });
                }
            }
            workbook.close();
        } catch (Exception e) {
            Toast.makeText(MyApplication.getAppContext(), "Error when importing the data", Toast.LENGTH_LONG).show();
        }
    }


    public void importItemsData() {
        try {
            AssetManager assetManager = MyApplication.getAppContext().getAssets();
            InputStream dataInputStream = assetManager.open("1.xlsx");

            Workbook workbook = WorkbookFactory.create(dataInputStream);
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            System.out.println("Retrieving Sheets using for-each loop");
            for (final Sheet sheet : workbook) {
                if (sheet.getSheetName().equalsIgnoreCase("Items")) {
                    final Map<String, String> ingredientMap = new HashMap<>();
                    final Map<String, String> tagMap = new HashMap<>();
                    final Map<String, String> itemMap = new HashMap<>();
                    ingredientAdminDao.getIngredients(new OnResultListener<List<Ingredient>>() {
                        @Override
                        public void onCallback(List<Ingredient> ingredients) {
                            for (Ingredient ingredient : ingredients) {
                                ingredientMap.put(ingredient.getName().toLowerCase(), ingredient.getId());
                            }
                            tagAdminDao.getTags(new OnResultListener<List<Tag>>() {
                                @Override
                                public void onCallback(List<Tag> tags) {
                                    for (Tag tag : tags) {
                                        tagMap.put(tag.getName().toLowerCase(), tag.getId());
                                    }
                                    menuItemAdminDao.getAllItems(new OnResultListener<List<RestaurantItem>>() {
                                        @Override
                                        public void onCallback(List<RestaurantItem> items) {
                                            for (RestaurantItem item : items) {
                                                itemMap.put(item.getName().toLowerCase(), item.getId());
                                            }
                                            importIngredientsAndTags(sheet, ingredientMap, tagMap, itemMap);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
            workbook.close();
        } catch (Exception e) {
            Toast.makeText(MyApplication.getAppContext(), "Error when importing the data", Toast.LENGTH_LONG).show();
        }
    }

    public void importAllData() throws IOException, InvalidFormatException {
        try {
            AssetManager assetManager = MyApplication.getAppContext().getAssets();
            InputStream dataInputStream = assetManager.open("1.xlsx");

            Workbook workbook = WorkbookFactory.create(dataInputStream);
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            System.out.println("Retrieving Sheets using for-each loop");
            for (Sheet sheet : workbook) {
                if (sheet.getSheetName().equalsIgnoreCase("Ingredients")) {
                    importIngredientsData();
                } else if (sheet.getSheetName().equalsIgnoreCase("Tags")) {
                    importTagsData();
                } else if (sheet.getSheetName().equalsIgnoreCase("Items")) {
                    importItemsData();
                }
            }
            workbook.close();
        } catch (Exception e) {
            Toast.makeText(MyApplication.getAppContext(), "Error when importing the data", Toast.LENGTH_LONG).show();
        }
    }

    private static String[] itemHeader = {"name", "price", "description", "ingredients", "tags"};
    private static String[] ingredientHeader = {"name"};
    private static String[] tagHeader = {"name"};

    private void importIngredientsAndTags(final Sheet sheet, final Map<String, String> ingredientMap, final Map<String, String> tagMap, final Map<String, String> itemMap) {
        Toast.makeText(MyApplication.getAppContext(), "Trying to insert all items from excel", Toast.LENGTH_LONG).show();
        DataFormatter dataFormatter = new DataFormatter();
        int rowCounter = 0;
        Set<String> newIngredients = new HashSet<>();
        Set<String> newTags = new HashSet<>();
        for (Row row : sheet) {
            if (rowCounter++ == 0) {
                continue;
            }
            int columnIndex = 0;
            for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = dataFormatter.formatCellValue(cell);
                if (cellValue != null) {
                    cellValue = cellValue.trim();
                    String cellType = itemHeader[columnIndex++];
                    if (cellType.equalsIgnoreCase("ingredients")) {
                        String[] split = cellValue.split(",");
                        List<String> ingredientNames = Arrays.asList(split);
                        for (String name : ingredientNames) {
                            if (name != null && name.length() > 0) {
                                String ingredientId = ingredientMap.get(name.toLowerCase());
                                if (ingredientId == null) {
                                    newIngredients.add(name.trim());
                                }
                            }
                        }
                    } else if (cellType.equalsIgnoreCase("tags")) {
                        String[] split = cellValue.split(",");
                        List<String> tagNames = Arrays.asList(split);

                        for (String name : tagNames) {
                            if (name != null && name.length() > 0) {
                                String tagId = tagMap.get(name.toLowerCase());
                                if (tagId == null) {
                                    newTags.add(name.trim());
                                }
                            }
                        }
                    }

                }
            }

        }

        List<Ingredient> ingsToCreate = new ArrayList<>();
        for (String ingName : newIngredients) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(ingName);
            ingsToCreate.add(ingredient);
        }


        final List<Tag> tagsToCreate = new ArrayList<>();
        for (String tagName : newTags) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tagsToCreate.add(tag);
        }

        ingredientAdminDao.insertIngredients(ingsToCreate, new OnResultListener<List<Ingredient>>() {
            @Override
            public void onCallback(List<Ingredient> ingredients) {
                for (Ingredient ingredient : ingredients) {
                    System.out.println("Created Ingredient " + ingredient.getName());
                    ingredientMap.put(ingredient.getName().toLowerCase(), ingredient.getId());
                }
                tagAdminDao.insertTags(tagsToCreate, new OnResultListener<List<Tag>>() {
                    @Override
                    public void onCallback(List<Tag> tags) {
                        for (Tag tag : tags) {
                            System.out.println("Created Tag" + tag.getName());
                            tagMap.put(tag.getName().toLowerCase(), tag.getId());
                        }


                        importItemsData(sheet, ingredientMap, tagMap, itemMap);
                    }
                });
            }
        });
    }


    private void importItemsData(Sheet sheet, final Map<String, String> ingredientMap, final Map<String, String> tagMap, final Map<String, String> itemMap) {
        Toast.makeText(MyApplication.getAppContext(), "Trying to insert all items from excel", Toast.LENGTH_LONG).show();
        DataFormatter dataFormatter = new DataFormatter();
        int rowCounter = 0;
        for (Row row : sheet) {
            if (rowCounter++ == 0) {
                continue;
            }
            final List<String> ingredientList = new ArrayList<>();
            final List<String> tagList = new ArrayList<>();
            RestaurantItem item = new RestaurantItem();
            int columnIndex = 0;
            boolean alreadyPresent = false;
            Set<String> newTagsToCreate = new HashSet<>();
            for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String cellValue = dataFormatter.formatCellValue(cell);
                if (cellValue != null) {
                    cellValue = cellValue.trim();
                    if (itemMap.get(cellValue.toLowerCase()) == null) {
                        String cellType = itemHeader[columnIndex++];
                        if (cellType.equalsIgnoreCase("name")) {
                            item.setName(cellValue);
                        } else if (cellType.equalsIgnoreCase("description")) {
                            item.setDescription(cellValue);
                        } else if (cellType.equalsIgnoreCase("price")) {
                            item.setPrice(cellValue);
                        } else if (cellType.equalsIgnoreCase("ingredients")) {
                            String[] split = cellValue.split(",");
                            List<String> ingredientNames = Arrays.asList(split);
                            for (String name : ingredientNames) {
                                String ingredientId = ingredientMap.get(name.toLowerCase().trim());
                                if (ingredientId != null) {
                                    ingredientList.add(ingredientId);
                                }
                            }
                        } else if (cellType.equalsIgnoreCase("tags")) {
                            String[] split = cellValue.split(",");
                            List<String> tagNames = Arrays.asList(split);
                            for (String name : tagNames) {
                                String tagId = tagMap.get(name.toLowerCase().trim());
                                if (tagId != null) {
                                    tagList.add(tagId);
                                }
                            }
                        }
                    } else {
                        alreadyPresent = true;
                        Toast.makeText(MyApplication.getAppContext(), cellValue + " Item is already present. Skipping this item", Toast.LENGTH_LONG).show();
                        break;
                    }

                }


            }
            if (!alreadyPresent) {
                if (newTagsToCreate != null) {

                }
                menuItemAdminDao.insertOrUpdateMenuItem(item, new OnResultListener<RestaurantItem>() {
                    @Override
                    public void onCallback(RestaurantItem item) {
                        System.out.println("Created the item " + item.getName());
                        itemMap.put(item.getName().toLowerCase(), item.getId());
                        menuItemAdminDao.addIngredientsToItem(item.getId() + "", ingredientList);
                        menuItemAdminDao.addTagsToItem(item.getId() + "", tagList);
                    }
                });
            }
        }
        Toast.makeText(MyApplication.getAppContext(), "Inserted Items successfully", Toast.LENGTH_LONG).show();
    }

    private void importTagsData(Sheet sheet, final Map<String, String> tagMap) {
        Toast.makeText(MyApplication.getAppContext(), "Trying to insert tags", Toast.LENGTH_LONG).show();
        DataFormatter dataFormatter = new DataFormatter();
        int rowCounter = 0;
        for (Row row : sheet) {
            if (rowCounter++ == 0) {
                continue;
            }
            Tag tag = new Tag();
            int columnIndex = 0;
            boolean alreadyPresent = false;
            for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = dataFormatter.formatCellValue(cell);
                if (cellValue != null) {
                    cellValue = cellValue.trim();
                    if (tagMap.get(cellValue.toLowerCase()) == null) {
                        String cellType = tagHeader[columnIndex++];
                        if (cellType.equalsIgnoreCase("name")) {
                            tag.setName(cellValue);
                        }
                    } else {
                        alreadyPresent = true;
                        Toast.makeText(MyApplication.getAppContext(), cellValue + " is already present. Skipping this tag", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                tag.setName(cellValue);
            }
            if (!alreadyPresent) {
                tagAdminDao.insertTag(tag, new OnResultListener<Tag>() {
                    @Override
                    public void onCallback(Tag tag) {
                        tagMap.put(tag.getName().toLowerCase(), tag.getId());
                        System.out.println("Created tag " + tag.getName());
                    }
                });
            }

        }
        Toast.makeText(MyApplication.getAppContext(), "Inserted tags successfully", Toast.LENGTH_LONG).show();
    }

    private void importIngredientsData(Sheet sheet, final Map<String, String> ingredientMap) {
        Toast.makeText(MyApplication.getAppContext(), "Trying to insert ingredients", Toast.LENGTH_LONG).show();
        DataFormatter dataFormatter = new DataFormatter();
        int rowCounter = 0;
        for (Row row : sheet) {
            if (rowCounter++ == 0) {
                continue;
            }
            Ingredient ingredient = new Ingredient();
            int columnIndex = 0;
            boolean alreadyPresent = false;
            for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = dataFormatter.formatCellValue(cell);
                if (cellValue != null) {
                    cellValue = cellValue.trim();
                    if (ingredientMap.get(cellValue.toLowerCase()) == null) {
                        String cellType = ingredientHeader[columnIndex++];
                        if (cellType.equalsIgnoreCase("name")) {
                            ingredient.setName(cellValue);
                        }
                    } else {
                        Toast.makeText(MyApplication.getAppContext(), cellValue + " is already present. Skipping this ingredient", Toast.LENGTH_LONG).show();
                        alreadyPresent = true;
                        break;
                    }
                }
                ingredient.setName(cellValue);
            }
            if (!alreadyPresent) {
                ingredientAdminDao.insertIngredient(ingredient, new OnResultListener<Ingredient>() {
                    @Override
                    public void onCallback(Ingredient ingredient) {
                        ingredientMap.put(ingredient.getName().toLowerCase(), ingredient.getId());
                        System.out.println("Created Ingredient " + ingredient.getName());
                    }
                });
            }

        }
        Toast.makeText(MyApplication.getAppContext(), "Inserted ingredients successfully", Toast.LENGTH_LONG).show();
    }
}