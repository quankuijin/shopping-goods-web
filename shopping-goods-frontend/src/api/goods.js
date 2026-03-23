import request from './request'

export const getGoodsList = (params) => {
  return request.get('/goods/list', { params })
}

export const getGoodsById = (id) => {
  return request.get(`/goods/${id}`)
}

export const createGoods = (data) => {
  return request.post('/goods', data)
}

export const updateGoods = (id, data) => {
  return request.put(`/goods/${id}`, data)
}

export const deleteGoods = (id) => {
  return request.delete(`/goods/${id}`)
}
