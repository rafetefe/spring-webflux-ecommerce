curl -X POST localhost:8080/product \
	-H "Content-Type: application/json" \
	--data '{"productId":175, "name":"erroneousProduct", "price":11.2, "falseAttribute":"undefinedValue"}'
echo ""
