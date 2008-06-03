/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class Model1PackageImpl extends EPackageImpl implements Model1Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass addressEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass supplierEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass purchaseOrderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass orderDetailEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass productEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass orderAddressEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EEnum vatEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass categoryEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass companyEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass customerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass orderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass salesOrderEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model1PackageImpl()
  {
    super(eNS_URI, Model1Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * Simple dependencies are satisfied by calling this method on all dependent packages before doing anything else. This
   * method drives initialization for interdependent packages directly, in parallel with this package, itself.
   * <p>
   * Of this package and its interdependencies, all packages which have not yet been registered by their URI values are
   * first created and registered. The packages are then initialized in two steps: meta-model objects for all of the
   * packages are created before any are initialized, since one package's meta-model objects may refer to those of
   * another.
   * <p>
   * Invocation of this method will not affect any packages that have already been initialized. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model1Package init()
  {
    if (isInited) return (Model1Package)EPackage.Registry.INSTANCE.getEPackage(Model1Package.eNS_URI);

    // Obtain or create and register package
    Model1PackageImpl theModel1Package = (Model1PackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof Model1PackageImpl ? EPackage.Registry.INSTANCE
        .getEPackage(eNS_URI)
        : new Model1PackageImpl());

    isInited = true;

    // Create package meta-data objects
    theModel1Package.createPackageContents();

    // Initialize created meta-data
    theModel1Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel1Package.freeze();

    return theModel1Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getAddress()
  {
    return addressEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getAddress_Name()
  {
    return (EAttribute)addressEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getAddress_Street()
  {
    return (EAttribute)addressEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getAddress_City()
  {
    return (EAttribute)addressEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getSupplier()
  {
    return supplierEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getSupplier_PurchaseOrders()
  {
    return (EReference)supplierEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getSupplier_Preferred()
  {
    return (EAttribute)supplierEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getPurchaseOrder()
  {
    return purchaseOrderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getPurchaseOrder_Date()
  {
    return (EAttribute)purchaseOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getPurchaseOrder_Supplier()
  {
    return (EReference)purchaseOrderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getOrderDetail()
  {
    return orderDetailEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getOrderDetail_Order()
  {
    return (EReference)orderDetailEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getOrderDetail_Product()
  {
    return (EReference)orderDetailEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getOrderDetail_Price()
  {
    return (EAttribute)orderDetailEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getProduct()
  {
    return productEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getProduct_Name()
  {
    return (EAttribute)productEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getProduct_OrderDetails()
  {
    return (EReference)productEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getProduct_Vat()
  {
    return (EAttribute)productEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getOrderAddress()
  {
    return orderAddressEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getOrderAddress_TestAttribute()
  {
    return (EAttribute)orderAddressEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EEnum getVAT()
  {
    return vatEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCategory()
  {
    return categoryEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCategory_Name()
  {
    return (EAttribute)categoryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCategory_Categories()
  {
    return (EReference)categoryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCategory_Products()
  {
    return (EReference)categoryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCompany()
  {
    return companyEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCompany_Categories()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCompany_Suppliers()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCompany_PurchaseOrders()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCompany_Customers()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCompany_SalesOrders()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCustomer()
  {
    return customerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCustomer_SalesOrders()
  {
    return (EReference)customerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getOrder()
  {
    return orderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getOrder_OrderDetails()
  {
    return (EReference)orderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getSalesOrder()
  {
    return salesOrderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getSalesOrder_Id()
  {
    return (EAttribute)salesOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getSalesOrder_Customer()
  {
    return (EReference)salesOrderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model1Factory getModel1Factory()
  {
    return (Model1Factory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    addressEClass = createEClass(ADDRESS);
    createEAttribute(addressEClass, ADDRESS__NAME);
    createEAttribute(addressEClass, ADDRESS__STREET);
    createEAttribute(addressEClass, ADDRESS__CITY);

    companyEClass = createEClass(COMPANY);
    createEReference(companyEClass, COMPANY__CATEGORIES);
    createEReference(companyEClass, COMPANY__SUPPLIERS);
    createEReference(companyEClass, COMPANY__CUSTOMERS);
    createEReference(companyEClass, COMPANY__PURCHASE_ORDERS);
    createEReference(companyEClass, COMPANY__SALES_ORDERS);

    supplierEClass = createEClass(SUPPLIER);
    createEReference(supplierEClass, SUPPLIER__PURCHASE_ORDERS);
    createEAttribute(supplierEClass, SUPPLIER__PREFERRED);

    customerEClass = createEClass(CUSTOMER);
    createEReference(customerEClass, CUSTOMER__SALES_ORDERS);

    orderEClass = createEClass(ORDER);
    createEReference(orderEClass, ORDER__ORDER_DETAILS);

    orderDetailEClass = createEClass(ORDER_DETAIL);
    createEReference(orderDetailEClass, ORDER_DETAIL__ORDER);
    createEReference(orderDetailEClass, ORDER_DETAIL__PRODUCT);
    createEAttribute(orderDetailEClass, ORDER_DETAIL__PRICE);

    purchaseOrderEClass = createEClass(PURCHASE_ORDER);
    createEAttribute(purchaseOrderEClass, PURCHASE_ORDER__DATE);
    createEReference(purchaseOrderEClass, PURCHASE_ORDER__SUPPLIER);

    salesOrderEClass = createEClass(SALES_ORDER);
    createEAttribute(salesOrderEClass, SALES_ORDER__ID);
    createEReference(salesOrderEClass, SALES_ORDER__CUSTOMER);

    categoryEClass = createEClass(CATEGORY);
    createEAttribute(categoryEClass, CATEGORY__NAME);
    createEReference(categoryEClass, CATEGORY__CATEGORIES);
    createEReference(categoryEClass, CATEGORY__PRODUCTS);

    productEClass = createEClass(PRODUCT);
    createEAttribute(productEClass, PRODUCT__NAME);
    createEReference(productEClass, PRODUCT__ORDER_DETAILS);
    createEAttribute(productEClass, PRODUCT__VAT);

    orderAddressEClass = createEClass(ORDER_ADDRESS);
    createEAttribute(orderAddressEClass, ORDER_ADDRESS__TEST_ATTRIBUTE);

    // Create enums
    vatEEnum = createEEnum(VAT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    companyEClass.getESuperTypes().add(this.getAddress());
    supplierEClass.getESuperTypes().add(this.getAddress());
    customerEClass.getESuperTypes().add(this.getAddress());
    purchaseOrderEClass.getESuperTypes().add(this.getOrder());
    salesOrderEClass.getESuperTypes().add(this.getOrder());
    orderAddressEClass.getESuperTypes().add(this.getAddress());
    orderAddressEClass.getESuperTypes().add(this.getOrder());
    orderAddressEClass.getESuperTypes().add(this.getOrderDetail());

    // Initialize classes and features; add operations and parameters
    initEClass(addressEClass, Address.class, "Address", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAddress_Name(), ecorePackage.getEString(), "name", null, 0, 1, Address.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddress_Street(), ecorePackage.getEString(), "street", null, 0, 1, Address.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddress_City(), ecorePackage.getEString(), "city", null, 0, 1, Address.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(companyEClass, Company.class, "Company", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCompany_Categories(), this.getCategory(), null, "categories", null, 0, -1, Company.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_Suppliers(), this.getSupplier(), null, "suppliers", null, 0, -1, Company.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_Customers(), this.getCustomer(), null, "customers", null, 0, -1, Company.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_PurchaseOrders(), this.getPurchaseOrder(), null, "purchaseOrders", null, 0, -1,
        Company.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_SalesOrders(), this.getSalesOrder(), null, "salesOrders", null, 0, -1, Company.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(supplierEClass, Supplier.class, "Supplier", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSupplier_PurchaseOrders(), this.getPurchaseOrder(), this.getPurchaseOrder_Supplier(),
        "purchaseOrders", null, 0, -1, Supplier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSupplier_Preferred(), ecorePackage.getEBoolean(), "preferred", "true", 0, 1, Supplier.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(customerEClass, Customer.class, "Customer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCustomer_SalesOrders(), this.getSalesOrder(), this.getSalesOrder_Customer(), "salesOrders", null,
        0, -1, Customer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderEClass, Order.class, "Order", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrder_OrderDetails(), this.getOrderDetail(), this.getOrderDetail_Order(), "orderDetails", null,
        0, -1, Order.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderDetailEClass, OrderDetail.class, "OrderDetail", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrderDetail_Order(), this.getOrder(), this.getOrder_OrderDetails(), "order", null, 1, 1,
        OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrderDetail_Product(), this.getProduct(), this.getProduct_OrderDetails(), "product", null, 0, 1,
        OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOrderDetail_Price(), ecorePackage.getEFloat(), "price", null, 0, 1, OrderDetail.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(purchaseOrderEClass, PurchaseOrder.class, "PurchaseOrder", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPurchaseOrder_Date(), ecorePackage.getEDate(), "date", null, 0, 1, PurchaseOrder.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPurchaseOrder_Supplier(), this.getSupplier(), this.getSupplier_PurchaseOrders(), "supplier",
        null, 1, 1, PurchaseOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(salesOrderEClass, SalesOrder.class, "SalesOrder", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSalesOrder_Id(), ecorePackage.getEInt(), "id", null, 0, 1, SalesOrder.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSalesOrder_Customer(), this.getCustomer(), this.getCustomer_SalesOrders(), "customer", null, 1,
        1, SalesOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(categoryEClass, Category.class, "Category", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCategory_Name(), ecorePackage.getEString(), "name", null, 0, 1, Category.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCategory_Categories(), this.getCategory(), null, "categories", null, 0, -1, Category.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getCategory_Products(), this.getProduct(), null, "products", null, 0, -1, Category.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(productEClass, Product.class, "Product", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProduct_Name(), ecorePackage.getEString(), "name", null, 0, 1, Product.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProduct_OrderDetails(), this.getOrderDetail(), this.getOrderDetail_Product(), "orderDetails",
        null, 0, -1, Product.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProduct_Vat(), this.getVAT(), "vat", "vat15", 0, 1, Product.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderAddressEClass, OrderAddress.class, "OrderAddress", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getOrderAddress_TestAttribute(), ecorePackage.getEBoolean(), "testAttribute", null, 0, 1,
        OrderAddress.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(vatEEnum, org.eclipse.emf.cdo.tests.model1.VAT.class, "VAT");
    addEEnumLiteral(vatEEnum, org.eclipse.emf.cdo.tests.model1.VAT.VAT0);
    addEEnumLiteral(vatEEnum, org.eclipse.emf.cdo.tests.model1.VAT.VAT7);
    addEEnumLiteral(vatEEnum, org.eclipse.emf.cdo.tests.model1.VAT.VAT15);

    // Create resource
    createResource(eNS_URI);
  }

} // Model1PackageImpl
