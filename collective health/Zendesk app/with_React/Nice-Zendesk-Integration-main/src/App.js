import { Route, Switch } from 'react-router';

import Zendesk from './zendesk';
import TempElement from './tempElement';

export function App() {
  const qryParams = new URLSearchParams(window.location.search);
  let intBaseUrl = qryParams.get('base');
  intBaseUrl = intBaseUrl ? intBaseUrl : '';

  return (
    <Switch>
      <Route exact path="/temp" component={TempElement} />
      <Route path="/" render={() => <Zendesk openFrameBaseUrl={intBaseUrl || ''} />} />
    </Switch>
  );
}

export default App;