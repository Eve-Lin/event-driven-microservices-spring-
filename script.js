import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  stages: [
    { duration: '2m', target: 100 },
    { duration: '2m', target: 200 },
    { duration: '2m', target: 300 },
    { duration: '2m', target: 400 },
  ],
};

export default function () {
let customerId = generateCode();
  http.post('http://host.docker.internal:8080/orders', JSON.stringify({customerId: customerId,totalAmount: 90.00}),{ headers: { 'Content-Type': 'application/json' },});
  sleep(1);
}
function generateCode() {
  const letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  const digits = "0123456789";

  const array = new Uint32Array(4);
  crypto.getRandomValues(array);

  return (
    letters[array[0] % 26] +
    letters[array[1] % 26] +
    digits[array[2] % 10] +
    digits[array[3] % 10]
  );
}
