<template>
  <div class="goods-container">
    <el-header class="header">
      <div class="logo">商品管理系统</div>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </el-header>
    
    <el-main class="main-content">
      <div class="toolbar">
        <el-button type="primary" @click="openDialog">
          <el-icon><Plus /></el-icon>
          添加商品
        </el-button>
      </div>

      <el-table :data="goodsList" border stripe class="goods-table">
        <el-table-column prop="name" label="商品名称" width="150" />
        <el-table-column prop="code" label="商品编码" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="openDialog(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="goodsFormRef" :model="formModel" :rules="goodsRules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品编码" prop="code">
          <el-input v-model="formCode" placeholder="请输入商品编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计量单位" prop="unit">
          <el-input v-model="formUnit" placeholder="如：件、个、箱" style="width: 100px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formStatus">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片链接" prop="image">
          <el-input v-model="formImage" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="formDescription" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const goodsList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const goodsFormRef = ref(null)
const currentGoodsId = ref('')

const formName = ref('')
const formCode = ref('')
const formPrice = ref(0)
const formUnit = ref('件')
const formStatus = ref(1)
const formImage = ref('')
const formDescription = ref('')

const formModel = computed(() => ({
  name: formName.value,
  code: formCode.value,
  price: formPrice.value,
  unit: formUnit.value,
  status: formStatus.value,
  image: formImage.value,
  description: formDescription.value
}))

const goodsRules = reactive({
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }]
})

const loadGoodsList = async () => {
  try {
    const res = await axios.get('/api/goods')
    if (res.data.code === 200) {
      goodsList.value = res.data.data
    }
  } catch (err) {
    ElMessage.error('加载商品列表失败')
  }
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    currentGoodsId.value = row.id || ''
    formName.value = row.name || ''
    formCode.value = row.code || ''
    formPrice.value = row.price || 0
    formUnit.value = row.unit || '件'
    formStatus.value = row.status ?? 1
    formImage.value = row.image || ''
    formDescription.value = row.description || ''
  } else {
    currentGoodsId.value = ''
    formName.value = ''
    formCode.value = ''
    formPrice.value = 0
    formUnit.value = '件'
    formStatus.value = 1
    formImage.value = ''
    formDescription.value = ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    let res
    const data = {
      name: formName.value,
      code: formCode.value,
      price: formPrice.value,
      unit: formUnit.value,
      status: formStatus.value,
      image: formImage.value,
      description: formDescription.value
    }
    if (isEdit.value) {
      data.id = currentGoodsId.value
      res = await axios.put(`/api/goods/${currentGoodsId.value}`, data)
    } else {
      res = await axios.post('/api/goods', data)
    }
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      loadGoodsList()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (err) {
    ElMessage.error('操作失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.delete(`/api/goods/${id}`)
      if (res.data.code === 200) {
        ElMessage.success('删除成功')
        loadGoodsList()
      }
    } catch (err) {
      ElMessage.error('删除失败')
    }
  })
}

const handleLogout = () => {
  localStorage.removeItem('isLoggedIn')
  router.push('/login')
}

onMounted(() => {
  loadGoodsList()
})
</script>

<style scoped>
.goods-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #ff6b00 0%, #ff8c00 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.main-content {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.goods-table {
  background: #fff;
}
</style>
