import request from '../utils/request'

export function getGoodsList(params) {
  return request({
    url: '/goods',
    method: 'get',
    params
  })
}

export function getGoodsById(id) {
  return request({
    url: `/goods/${id}`,
    method: 'get'
  })
}

export function createGoods(data) {
  return request({
    url: '/goods',
    method: 'post',
    data
  })
}

export function updateGoods(id, data) {
  return request({
    url: `/goods/${id}`,
    method: 'put',
    data
  })
}

export function deleteGoods(id) {
  return request({
    url: `/goods/${id}`,
    method: 'delete'
  })
}

export function updateGoodsStatus(id, status) {
  return request({
    url: `/goods/${id}/status`,
    method: 'put',
    params: { status }
  })
}
