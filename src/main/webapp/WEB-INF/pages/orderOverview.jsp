<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>

<tags:master pageTitle="Order overview">

    <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
    <h1>Order overview</h1>
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
        <tags:orderOverviewRow name="firstName" label="First name" order="${order}"></tags:orderOverviewRow>
        <tags:orderOverviewRow name="lastName" label="Last name" order="${order}"></tags:orderOverviewRow>
        <tags:orderOverviewRow name="phone" label="Phone" order="${order}"></tags:orderOverviewRow>
        <tags:orderOverviewRow name="deliveryDate" label="Delivery date" order="${order}"></tags:orderOverviewRow>
        <tags:orderOverviewRow name="deliveryAddress" label="Delivery address" order="${order}"></tags:orderOverviewRow>

        <tr>
            <td>Payment method</td>
             <td>${order.paymentMethod}</td>
        </td>
        </tr>
    </table>
</tags:master>