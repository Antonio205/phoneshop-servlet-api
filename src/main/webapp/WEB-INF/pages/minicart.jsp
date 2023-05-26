<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="session"/>

<a href="${pageContext.servletContext.contextPath}/cart">Cart: ${cart.totalQuantity} items</a>