<!--

    Copyright CIB software GmbH and/or licensed to CIB software GmbH
    under one or more contributor license agreements. See the NOTICE file
    distributed with this work for additional information regarding copyright
    ownership. CIB software licenses this file to you under the Apache License,
    Version 2.0; you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.

-->
<template>
  <div class="overflow-auto bg-white container-fluid g-0">
    <div v-if="loading">
      <p class="text-center p-4"><BWaitingBox class="d-inline me-2" styling="width: 35px"></BWaitingBox> {{ $t('admin.loading') }}</p>
    </div>
    <FlowTable v-else-if="calledProcessDefinitions.length > 0" resizable striped thead-class="sticky-header" :items="calledProcessDefinitions" primary-key="id" prefix="process-instance.calledProcessDefinitions."
      sort-by="label" :sort-desc="true" :fields="[
        { label: 'calledProcessDefinition', key: 'name', class: 'col-4', thClass: 'border-end', tdClass: 'py-1 border-end border-top-0' },
        { label: 'state', key: 'state', class: 'col-4', thClass: 'border-end', tdClass: 'py-1 border-end border-top-0' },
        { label: 'activity', key: 'activity', class: 'col-4', thClass: 'border-end', tdClass: 'py-1 border-end border-top-0' }
      ]">
      <template v-slot:cell(name)="table">
        <CopyableActionButton
            :display-value="table.item.name || table.item.key"
            :title="table.item.name"
            @copy="copyValueToClipboard"
            :to="{
              name: 'process', 
              params: {
                processKey: table.item.key,
                versionIndex: table.item.version
              }
            }"
          />
      </template>
      <template v-slot:cell(state)="table">
        <span v-if="table.item.activities.length" class="text-truncate">
          {{ $t(getCalledProcessState(table.item.activities)) }}
        </span>
      </template>
      <template v-slot:cell(activity)="table">
        <div class="w-100">
          <CopyableActionButton
            v-for="(act, index) in table.item.activities" :key="index" 
            :display-value="act.activityName"
            :title="act.activityName"
            @click="selectActivity(act.activityId)"
            @copy="copyValueToClipboard"
          />
        </div>
      </template>
    </FlowTable>
    <div v-else-if="!loading">
      <p class="text-center p-4">{{ $t('process-instance.noResults') }}</p>
    </div>
  </div>
  <SuccessAlert ref="messageCopy" style="z-index: 9999"> {{ $t('process.copySuccess') }} </SuccessAlert>
</template>

<script>
import FlowTable from '@/components/common-components/FlowTable.vue'
import { BWaitingBox } from 'cib-common-components'
import { mapActions, mapGetters } from 'vuex'
import CopyableActionButton from '@/components/common-components/CopyableActionButton.vue'
import copyToClipboardMixin from '@/mixins/copyToClipboardMixin.js'
import SuccessAlert from '@/components/common-components/SuccessAlert.vue'


export default {
  name: 'CalledProcessDefinitionsTable',
  components: { FlowTable, CopyableActionButton, SuccessAlert, BWaitingBox },
  mixins: [copyToClipboardMixin],
  props: {
    process: Object
  },
  data() {
    return {
      loading: false
    }
  },
  computed: {
    ...mapGetters('calledProcessDefinitions', [
      'calledProcessDefinitions', 
      'allCalledProcessDefinitions', 
      'getCalledProcessState'
    ]),
    ...mapGetters(['diagramXml', 'selectedActivityId']),
  },
  watch: {
    selectedActivityId() {
      this.setHighlightedElement(this.selectedActivityId)
      this.filterByActivity(this.selectedActivityId)
    },
    'process.id': {
      handler(id) {
        if (id) {
          this.loadCalledProcessDefinitionsData(id)
        }
      },
      immediate: true
    }
  },
  methods: {
    ...mapActions(['setHighlightedElement', 'selectActivity']),
    ...mapActions('calledProcessDefinitions', [
      'loadCalledProcessDefinitions', 
            'filterByActivity'
    ]),
    async loadCalledProcessDefinitionsData(processId) {
      this.loading = true
      try {
        await this.loadCalledProcessDefinitions({ 
          processId, 
          diagramXml: this.diagramXml 
        })
      } catch (error) {
        console.error('Error loading called processes:', error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>