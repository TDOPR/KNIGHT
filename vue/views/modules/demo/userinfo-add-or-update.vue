<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.userId ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="" prop="userAddress">
          <el-input v-model="dataForm.userAddress" placeholder=""></el-input>
      </el-form-item>
          <el-form-item label="" prop="referAddress">
          <el-input v-model="dataForm.referAddress" placeholder=""></el-input>
      </el-form-item>
          <el-form-item label="" prop="betVal">
          <el-input v-model="dataForm.betVal" placeholder=""></el-input>
      </el-form-item>
          <el-form-item label="" prop="botLevel">
          <el-input v-model="dataForm.botLevel" placeholder=""></el-input>
      </el-form-item>
          <el-form-item label="" prop="allBuy">
          <el-input v-model="dataForm.allBuy" placeholder=""></el-input>
      </el-form-item>
      </el-form>
    <template slot="footer">
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script>
import debounce from 'lodash/debounce'
export default {
  data () {
    return {
      visible: false,
      dataForm: {
        userId: '',
        userAddress: '',
        referAddress: '',
        betVal: '',
        botLevel: '',
        allBuy: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        userAddress: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        referAddress: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        betVal: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        botLevel: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        allBuy: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.userId) {
          this.getInfo()
        }
      })
    },
    // 获取信息
    getInfo () {
      this.$http.get(`/demo/userinfo/${this.dataForm.userId}`).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.msg)
        }
        this.dataForm = {
          ...this.dataForm,
          ...res.data
        }
      }).catch(() => {})
    },
    // 表单提交
    dataFormSubmitHandle: debounce(function () {
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return false
        }
        this.$http[!this.dataForm.userId ? 'post' : 'put']('/demo/userinfo/', this.dataForm).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.msg)
          }
          this.$message({
            message: this.$t('prompt.success'),
            type: 'success',
            duration: 500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList')
            }
          })
        }).catch(() => {})
      })
    }, 1000, { 'leading': true, 'trailing': false })
  }
}
</script>
