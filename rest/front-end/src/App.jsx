import React from 'react'

import { Provider } from 'react-redux'
import { legacy_createStore as createStore, combineReducers, applyMiddleware } from 'redux'
import { thunk } from 'redux-thunk'
import { HashRouter, Routes, Route } from 'react-router'

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

import Blocker from './components/Blocker'

import NavBar from './components/NavBar'

import reducers from './reducers'

const store = createStore(
  combineReducers(reducers),
  applyMiddleware(thunk)
)

const Layout = () => (
  <div className='container-fluid'>
    <NavBar />
    <div className='container-fluid' style={{ marginTop: '50px' }}>
      <Routes>
        <Route path='/' element={<Description />} />
        <Route path='/configuration' element={<Configuration />} />
        <Route path='/configurationAnon' element={<ListAnonComponent />} />
        <Route path='/configurationAnon/:anonymizationAlgorithm' element={<ConfigurationForAnonymization />} />
        <Route path='/identifiers' element={<Identifiers />} />
        <Route path='/maskingProviders' element={<MaskingProviders />} />
        <Route path='/anonymizationProviders' element={<AnonymizationProviders />} />
        <Route path='/workflow/*' element={<Workflow />} />
        <Route path='/exploration' element={<Explore />} />
        <Route path='*' element={<NotFound />} />
      </Routes>
    </div>
    <Blocker />
  </div>
)

const App = () => (
  <Provider store={store}>
    <HashRouter>
      <Layout />
    </HashRouter>
  </Provider>
)

export default App
