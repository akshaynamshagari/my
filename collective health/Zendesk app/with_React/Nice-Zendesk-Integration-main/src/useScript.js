import { useEffect, useState } from 'react';

export default function useScript(props) {
  // Keep track of script status ("idle", "loading", "ready", "error")
  const [status, setStatus] = useState(props.scriptSrc ? 'loading' : 'idle');
  useEffect(() => {
    // Allow falsy src value if waiting on other data needed for
    // constructing the script URL passed to this hook.
    if (!props.scriptSrc) {
      setStatus('idle');
      return;
    }
    console.log('usescript:' + status);
    console.log('[useScript] Injecting script: ', props.scriptSrc);
    const head = document.querySelector('head');
    const script = document.createElement('script');
    if (script) {
      script.setAttribute('src', props.scriptSrc);
      props.attributes.forEach((attr) => {
        script.setAttribute(attr.name, attr.value);
      });
      head?.appendChild(script);
      // Store status in attribute on script
      // This can be read by other instances of this hook
      const setAttributeFromEvent = (event) => {
        script.setAttribute(
          'data-status',
          event.type === 'load' ? 'ready' : 'error'
        );
      };
      script.addEventListener('load', setAttributeFromEvent);
      script.addEventListener('error', setAttributeFromEvent);
    } else {
      // Grab existing script status from attribute and set to state.
      //setStatus(script.getAttribute("data-status"));
    }
    // Script event handler to update status in state
    // Note: Even if the script already exists we still need to add
    // event handlers to update the state for *this* hook instance.
    const setStateFromEvent = (event) => {
      setStatus(event.type === 'load' ? 'ready' : 'error');
    };
    // Add event listeners
    script.addEventListener('load', setStateFromEvent);
    script.addEventListener('error', setStateFromEvent);
  }, [props.scriptSrc]);
  return status;
}