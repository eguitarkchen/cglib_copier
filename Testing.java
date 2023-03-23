    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = "test";
        List<ShipmentForCounterPointRelatedDTO> sourceList = new ArrayList<>();

        for (long i = 0; i < 10; i++) {
            ShipmentForCounterPointRelatedDTO source = new ShipmentForCounterPointRelatedDTO();
            source.setShipmentEntryId(i);
            source.setCounterpointId(i);
            source.setCounterpointDetailId(i);
            source.setCounterpointNumber(i+str);
            sourceList.add(source);
        }
        List<BusinessRelated> targetList = BeanUtils.cgLibCopyList(sourceList, BusinessRelated::new);

        log.info("sourceList:{}", objectMapper.writeValueAsString(sourceList));
        log.info("targetList:{}", objectMapper.writeValueAsString(targetList));
    }
