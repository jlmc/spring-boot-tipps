# How to run

1. just launch the application

2. execute the following curl request command 

```shell script
curl --location --request POST 'localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
	"client": "Sr Manuel",
    "items": [
        {
            "product": "Tv",
            "unitValue": 15.83,
            "qty": 3
        },
        {
            "product": "Box",
            "unitValue": 5.01,
            "qty": 2
        }
    ]
}'
```