<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>

<tags:master pageTitle="Checkout">
    <c:if test="${not empty param.message && empty errors}">
        <div class="success">
            ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="error">
            An error occurred while placing order
        </div>
    </c:if>
    <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
    <form method="post" action="${contextPath}/checkout">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    Description
                </td>
                <td class="quantity">
                    Quantity
                </td>
                <td class="price">
                    Price
                </td>
            </tr>
            </thead>
            <c:forEach var="item" items="${order.items}">
                <c:set var="product" value="${item.product}"/>
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}">
                    </td>
                    <td>
                        <a href="${contextPath}/products/${product.id}">
                            ${product.description}
                        </a>
                    </td>
                    <td class="quantity">
                        ${item.quantity}
                    </td>
                    <td class="price">
                        <a href="${contextPath}/priceHistory/${product.id}"/>
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <tr>
                <td></td>
                <td>
                <td>
                    Total quantity:
                </td>
                <td class="quantity">
                    ${cart.totalQuantity}</td>
                </td>
            </tr>

            <tr>
                <td></td>
                <td></td>
                <td>
                    Subtotal:
                </td>
                <td class="price">
                    <fmt:formatNumber value="${order.subtotal}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>

            <tr>
                <td></td>
                <td></td>
                <td>
                    Delivery cost:
                </td>
                <td class="price">
                    <fmt:formatNumber value="${order.deliveryCost}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>

            <tr>
                <td></td>
                <td></td>
                <td>
                    Total cost:
                </td>
                <td class="price">
                    <fmt:formatNumber value="${order.totalCost}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </table>

        <h2>Yor details:</h2>
        <table>
            <tags:orderFormRow name="firstName" label="First name" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tags:orderFormRow name="lastName" label="Last name" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}"></tags:orderFormRow>

            <tr>
                <td>Delivery date<span style="color:red">*</span></td>
                <td>
                    <c:set var="error" value="${errors['deliveryDate']}"/>
                    <input name="deliveryDate" type="date" value="${not empty error ? param.deliveryDate : order.deliveryDate}"/>
                    <c:if test="${not empty error}">
                        <div class="error">
                            ${error}
                        </div>
                    </c:if>
                </td>
            </tr>

            <tags:orderFormRow name="deliveryAddress" label="Delivery address" order="${order}" errors="${errors}"></tags:orderFormRow>

            <tr>
                <td>Payment method<span style="color:red">*</span></td>
                <td>
                    <c:set var="paymentError" value="${errors['paymentMethod']}"/>
                    <select name="paymentMethod">
                        <option></option>
                        <c:forEach var="paymentMethod" items="${paymentMethods}">
                            <option value="${paymentMethod}" <c:if test="${paymentError == null && paymentMethod eq order.paymentMethod}">selected</c:if>>${paymentMethod}</option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty paymentError}">
                        <div class="error">
                            ${paymentError}
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>
        <p>
            <button>Place order</button>
        </p>
    </form>
</tags:master>