import { useEffect } from "react";

let client;

const TempElement = () => {
  useEffect(() => {
    client = window.ZAFClient.init();
    client && client.invoke('routeTo', 'ticket', 780)
  })

  console.log('component==> TempElement.js');

  return (
    <h1>Temporary Element</h1>
  )
}

export default TempElement;