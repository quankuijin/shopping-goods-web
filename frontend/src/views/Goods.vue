<template>
  <div class="goods-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品编码">
          <el-input v-model="queryParams.code" placeholder="请输入商品编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增商品
      </el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="name" label="商品名称" width="150" show-overflow-tooltip />
        <el-table-column prop="code" label="商品编码" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price ? row.price.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="计量单位" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="imageUrl" label="图片" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              style="width: 50px; height: 50px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入商品编码" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计量单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入计量单位" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片链接" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片链接" />
        </el-form-item>
        <el-form-item label="商品说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGoodsList, createGoods, updateGoods, deleteGoods, updateGoodsStatus } from '../api/goods'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  name: '',
  code: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  id: '',
  name: '',
  code: '',
  price: 0,
  unit: '',
  status: 0,
  imageUrl: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }]
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  if (Array.isArray(dateTime)) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = dateTime
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`
  }
  return dateTime
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getGoodsList(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryParams.name = ''
  queryParams.code = ''
  queryParams.status = null
  queryParams.pageNum = 1
  fetchData()
}

const resetForm = () => {
  form.id = ''
  form.name = ''
  form.code = ''
  form.price = 0
  form.unit = ''
  form.status = 0
  form.imageUrl = ''
  form.description = ''
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增商品'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.price = row.price
  form.unit = row.unit
  form.status = row.status
  form.imageUrl = row.imageUrl
  form.description = row.description
  dialogTitle.value = '编辑商品'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await updateGoods(form.id, form)
          ElMessage.success('修改成功')
        } else {
          await createGoods(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '上架' : '下架'
  
  try {
    await ElMessageBox.confirm(`确定要${actionText}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateGoodsStatus(row.id, newStatus)
    ElMessage.success(`${actionText}成功`)
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteGoods(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.goods-container {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 4px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-toolbar {
  margin-bottom: 16px;
}

.table-container {
  margin-top: 16px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
