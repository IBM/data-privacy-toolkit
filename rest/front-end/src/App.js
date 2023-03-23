import React from 'react'

import { Provider } from 'react-redux'
import { createStore, combineReducers, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'

import { Route, Switch } from 'react-router'
import createHashHistory from 'history/createHashHistory'

import { ConnectedRouter, routerReducer, routerMiddleware } from 'react-router-redux'

import Description from './pages/Description'
import Configuration from './pages/Configuration'
import ListAnonComponent from './pages/ListAnonComponent'
import ConfigurationForAnonymization from './pages/ConfigurationForAnonymization'
import Identifiers from './pages/Identifiers'
import MaskingProviders from './pages/MaskingProviders'
import AnonymizationProviders from './pages/AnonymizationProviders'
import NotFound from './pages/NotFound'
import Explore from './pages/Explore'
import Workflow from './pages/Workflow'

import Test from './pages/Test'

import Blocker from './components/Blocker'

import NavBar from './components/NavBar'

import reducers from './reducers'

const history = createHashHistory()

const loggerMiddleware = createLogger()
const router = routerMiddleware(history)

const initialState = {}

const store = createStore(
  combineReducers({
    ...reducers,
    router: routerReducer
  }),
  initialState,
  applyMiddleware(
    router,
    thunkMiddleware,
    loggerMiddleware
  )
)

const Layout = () => (
  <div className='container-fluid'>
    <NavBar />
    <div className='container-fluid' style={{marginTop: '50px'}}>
      <Switch>
        <Route exact path='/' component={Description} />
        <Route path='/configuration' component={Configuration} />
        <Route exact path='/configurationAnon' component={ListAnonComponent} />
        <Route path='/configurationAnon/:anonymizationAlgorithm' component={ConfigurationForAnonymization} />
        <Route path='/identifiers' component={Identifiers} />
        <Route path='/maskingProviders' component={MaskingProviders} />
        <Route path='/anonymizationProviders' component={AnonymizationProviders} />
        <Route path='/workflow' component={Workflow} />
        <Route path='/exploration' component={Explore} />
        <Route path='/test' component={Test} />

        <Route component={NotFound} />
      </Switch>
    </div>
    <Blocker />
  </div>
)

const App = () => (
  <Provider store={store}>
    <ConnectedRouter history={history}>
      <Layout />
    </ConnectedRouter>
  </Provider>
    )

export default App
