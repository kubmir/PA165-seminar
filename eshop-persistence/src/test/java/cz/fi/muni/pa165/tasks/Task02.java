package cz.fi.muni.pa165.tasks;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintViolationException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.fi.muni.pa165.PersistenceSampleApplicationContext;
import cz.fi.muni.pa165.entity.Category;
import cz.fi.muni.pa165.entity.Product;

@ContextConfiguration(classes = PersistenceSampleApplicationContext.class)
public class Task02 extends AbstractTestNGSpringContextTests {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private Category electro;
    private Category kitchen;
    private Product flashlight;
    private Product kitchenRobot;
    private Product plate;

    @BeforeClass
    public void prepareTestData() {
        EntityManager manager = emf.createEntityManager();
        
        manager.getTransaction().begin();
        electro = new Category();
        electro.setName("Electro");
        manager.persist(electro);

        kitchen = new Category();
        kitchen.setName("Kitchen");
        manager.persist(kitchen);

        flashlight = new Product();
        flashlight.setName("Flashlight");
        flashlight.addCategory(electro);
        manager.persist(flashlight);

        kitchenRobot = new Product();
        kitchenRobot.setName("KitchenRobot");
        kitchenRobot.addCategory(electro);
        kitchenRobot.addCategory(kitchen);
        manager.persist(kitchenRobot);

        plate = new Product();
        plate.setName("Plate");
        plate.addCategory(kitchen);
        manager.persist(plate);

        manager.getTransaction().commit();
        manager.close();
    }

    @Test
    public void testPlate() {
        EntityManager em = emf.createEntityManager();

        Product found = em.find(Product.class, plate.getId());
        Assert.assertEquals(found.getCategories().size(), 1);
        assertContainsCategoryWithName(found.getCategories(), "Kitchen");

        em.close();
    }

    @Test
    public void testFlashlight() {
        EntityManager em = emf.createEntityManager();

        Product found = em.find(Product.class, flashlight.getId());
        Assert.assertEquals(found.getCategories().size(), 1);
        assertContainsCategoryWithName(found.getCategories(), "Electro");

        em.close();
    }

    @Test
    public void testKitchenRobot() {
        EntityManager em = emf.createEntityManager();

        Product found = em.find(Product.class, kitchenRobot.getId());
        Assert.assertEquals(found.getCategories().size(), 2);
        assertContainsCategoryWithName(found.getCategories(), "Kitchen");
        assertContainsCategoryWithName(found.getCategories(), "Electro");

        em.close();
    }

    @Test
    public void testElectro() {
        EntityManager em = emf.createEntityManager();

        Category found = em.find(Category.class, electro.getId());
        Assert.assertEquals(found.getProducts().size(), 2);
        assertContainsProductWithName(found.getProducts(), "Flashlight");
        assertContainsProductWithName(found.getProducts(), "KitchenRobot");

        em.close();
    }

    @Test
    public void testKitchen() {
        EntityManager em = emf.createEntityManager();

        Category found = em.find(Category.class, kitchen.getId());
        Assert.assertEquals(found.getProducts().size(), 2);
        assertContainsProductWithName(found.getProducts(), "KitchenRobot");
        assertContainsProductWithName(found.getProducts(), "Plate");

        em.close();
    }

    private void assertContainsCategoryWithName(Set<Category> categories,
            String expectedCategoryName) {
        for (Category cat : categories) {
            if (cat.getName().equals(expectedCategoryName)) {
                return;
            }
        }

        Assert.fail("Couldn't find category " + expectedCategoryName + " in collection " + categories);
    }

    private void assertContainsProductWithName(Set<Product> products,
            String expectedProductName) {

        for (Product prod : products) {
            if (prod.getName().equals(expectedProductName)) {
                return;
            }
        }

        Assert.fail("Couldn't find product " + expectedProductName + " in collection " + products);
    }

}
