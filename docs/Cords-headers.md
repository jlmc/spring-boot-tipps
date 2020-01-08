# Cords Headers

NOTA: Origin = protocol:host:port

- ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

  - Header obrigatorio quando se tem clientes de origens cruzados.
  - quando ausente o clients sofrem de CORS
  - Quando a API é publica não dá para expecificar o valor deste header então usa-se o valor '*'
  - quando existe a possibilidade de saber exactamente quais os clients então usa-se a origin do request:
   